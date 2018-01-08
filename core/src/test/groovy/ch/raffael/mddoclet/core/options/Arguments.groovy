package ch.raffael.mddoclet.core.options

/**
 * @author Raffael Herzog
 */
class Arguments {

    final static string = Arguments.getMethod('string', String)
    final static simple = Arguments.getMethod('simple', MyEnum)
    final static multi = Arguments.getMethod('multi', EnumSetOption)
    final static illegalSimple = Arguments.getMethod('illegalSimple', Enum)
    final static illegalMulti = Arguments.getMethod('illegalMulti', EnumSetOption)
    final static multiArgs = Arguments.getMethod('multiArgs', String, MyEnum)

    String stringValue
    MyEnum enumValue
    Set<MyEnum> enumValues = EnumSet.noneOf(MyEnum)

    @OptionConsumer(names = '-string')
    void string(String value) {
        stringValue = value
    }

    @OptionConsumer(names = '-enum')
    void simple(MyEnum value) {
        enumValue = value
    }

    @OptionConsumer(names = '-enum-set')
    void multi(EnumSetOption<MyEnum> value) {
        value.applyTo(enumValues)
    }

    @OptionConsumer(names = '-multi-args')
    void multiArgs(String stringValue, MyEnum enumValue) {
        this.stringValue = stringValue
        this.enumValue = enumValue
    }

    void illegalSimple(Enum value) {
    }

    void illegalMulti(EnumSetOption<Enum> value) {
    }

    static enum MyEnum {
        FOO, BAR, FOO_BAR
    }

}
