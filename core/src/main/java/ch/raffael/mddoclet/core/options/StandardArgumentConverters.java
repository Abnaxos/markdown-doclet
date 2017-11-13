package ch.raffael.mddoclet.core.options;

import java.io.File;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;

import ch.raffael.nullity.Nullable;


/**
 * Standard converters for common Java types. Handles:
 *
 *  *  {@link String} -- no conversion is done.
 *
 *  *  {@link Integer} and the primitive `int` (uses {@link
 *     Integer#decode(String) decode()}, i.e. supports 0x... etc.
 *
 *  *  {@link Boolean} and the primitive `boolean`. Accepts the values
 *     "true", "yes", "y" and "on" as `true`, everything else is `false`.
 *
 *  *  {@link File}. Converts '/' to the platform specific file separator.
 *
 *  *  {@link Path}. Converts '/' to the platform specific file separator.
 *
 *  *  Enums. Converts the argument string to upper case and and replaces
 *     '-' with '_' (e.g. `my-enum-constant` becomes `MY_ENUM_CONSTANT`)
 *
 *  *  {@link EnumSetOption}. Handles lists of strings for adding and
 *     removing enum constants to/from a set. '+' adds the constant, '-'
 *     removes the constant. So, the argument `+add-me,-remove-me` would
 *     add the enum constant `ADD_ME` to a set and remove `REMOVE_ME`.
 *
 * @author Raffael Herzog
 */
public final class StandardArgumentConverters {

    private final static TypeVariable<Class<EnumSetOption>> ENUM_SET_ADJUSTMENTS_ENUM_TYPE = EnumSetOption.class.getTypeParameters()[0];

    private static final SimpleArgumentConverter<String> STRING_CONVERTER = standardConverter(String.class, (argument) -> argument);
    private static final SimpleArgumentConverter<Integer> INTEGER_CONVERTER = standardConverter(Integer.class, (argument) -> {
        try {
            return Integer.decode(argument);
        } catch (NumberFormatException e) {
            throw new InvalidOptionArgumentsException("'" + argument + "' is not a valid integer", e);
        }
    });
    private static final SimpleArgumentConverter<Boolean> BOOLEAN_CONVERTER =
            standardConverter(Boolean.class, (argument) -> {
                argument = argument.toLowerCase();
                switch (argument.toLowerCase()) {
                case "true":
                case "yes":
                case "y":
                case "on":
                    return true;
                default:
                    return false;
                }
            });
    private static final SimpleArgumentConverter<File> FILE_CONVERTER =
            standardConverter(File.class, (argument) -> new File(convertSlashPath(argument)));
    private static final SimpleArgumentConverter<Path> PATH_CONVERTER =
            standardConverter(Path.class, (argument) -> Paths.get(convertSlashPath(argument)));

    @Nullable
    public static ArgumentConverter<?> forType(Type type) {
        if (type.equals(String.class)) {
            return STRING_CONVERTER;
        } else if (type.equals(Integer.class)) {
            return INTEGER_CONVERTER;
        } else if (type.equals(Boolean.class)) {
            return BOOLEAN_CONVERTER;
        } else if (type.equals(File.class)) {
            return FILE_CONVERTER;
        } else if (type.equals(Path.class)) {
            return PATH_CONVERTER;
        }
        return tryForEnumType(type);
    }

    @Nullable
    private static <E extends Enum<E>> ArgumentConverter<?> tryForEnumType(Type type) {
        Class<E> enumClass = toEnumClass(type);
        if (enumClass != null) {
            return new EnumArgumentConverter<>(enumClass);
        }
        TypeToken token = TypeToken.of(type);
        if (EnumSetOption.class.isAssignableFrom(token.getRawType())) {
            enumClass = toEnumClass(token.resolveType(ENUM_SET_ADJUSTMENTS_ENUM_TYPE).getRawType());
            if (enumClass != null) {
                return new EnumSetArgumentConverter<>(enumClass);
            }
        }
        return null;
    }

    private static String convertSlashPath(String path) {
        return File.separatorChar == '/' ? path : path.replace('/', File.separatorChar);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <E extends Enum<E>> Class<E> toEnumClass(Type type) {
        if (type instanceof Class && !Enum.class.equals(type) && Enum.class.isAssignableFrom((Class)type)) {
            return (Class<E>)type;
        } else {
            return null;
        }
    }

    private static <T> SimpleArgumentConverter<T> standardConverter(Class<T> javaType, ArgumentConverter<T> delegate) {
        return new SimpleArgumentConverter<>(javaType, delegate);
    }

    private static final class SimpleArgumentConverter<T> implements ArgumentConverter<T> {

        private final Type javaType;
        private final ArgumentConverter<T> delegate;

        private SimpleArgumentConverter(Type javaType, ArgumentConverter<T> delegate) {
            this.javaType = javaType;
            this.delegate = delegate;
        }

        @Override
        public T convert(String argument) throws InvalidOptionArgumentsException {
            return delegate.convert(argument);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + javaType.getTypeName() + "]";
        }
    }

    public static final class EnumArgumentConverter<T extends Enum<T>> implements ArgumentConverter<T> {

        private final Class<T> enumClass;
        private final Map<String, T> enumConstants;

        public EnumArgumentConverter(Class<T> enumClass) {
            this.enumClass = enumClass;
            ImmutableMap.Builder<String, T> enumConstantsBuilder = ImmutableMap.builder();
            for (T enumConstant : enumClass.getEnumConstants()) {
                enumConstantsBuilder.put(enumConstant.name(), enumConstant);
            }
            this.enumConstants = enumConstantsBuilder.build();
        }

        @Override
        public T convert(String argument) throws InvalidOptionArgumentsException {
            T enumConstant = enumConstants.get(argument.toUpperCase().replace('-', '_'));
            if (enumConstant == null) {
                throw new InvalidOptionArgumentsException("Invalid value: '" + argument + "'");
            }
            return enumConstant;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + enumClass.getName() + "]";
        }
    }


    public static final class EnumSetArgumentConverter<E extends Enum<E>> implements ArgumentConverter<EnumSetOption<E>> {

        private static final Splitter SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

        private final Class<E> enumClass;
        private final ArgumentConverter<E> enumConverter;

        private EnumSetArgumentConverter(Class<E> enumClass) {
            this.enumClass = enumClass;
            enumConverter = new EnumArgumentConverter<>(enumClass);
        }

        @Override
        public EnumSetOption<E> convert(String argument) throws InvalidOptionArgumentsException {
            EnumSetOption<E> adjustments = new EnumSetOption<>();
            for (String enumConstantName : SPLITTER.split(argument)) {
                boolean add = true;
                if (enumConstantName.startsWith("-")) {
                    add = false;
                    enumConstantName = enumConstantName.substring(1);
                } else if (enumConstantName.startsWith("+")) {
                    enumConstantName = enumConstantName.substring(1);
                }
                adjustments.put(enumConverter.convert(enumConstantName), add);
            }
            return adjustments;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[" + enumClass.getName() + "]";
        }

    }

}
