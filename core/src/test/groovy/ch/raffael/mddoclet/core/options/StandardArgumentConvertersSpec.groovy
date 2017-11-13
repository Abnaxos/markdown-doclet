package ch.raffael.mddoclet.core.options

import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path
import java.nio.file.Paths

import static ch.raffael.mddoclet.core.options.Arguments.MyEnum
import static ch.raffael.mddoclet.core.options.Arguments.MyEnum.*


/**
 * @author Raffael Herzog
 */
class StandardArgumentConvertersSpec extends Specification {

    @Unroll
    def "Simple Java types and enums are converted correctly (#arg)"() {
      expect:
        StandardArgumentConverters.forType(type).convert(arg) == result

      where:
        type    | arg       || result

        Integer | '3'       || 3
        Integer | '-2'      || -2
        Integer | '0xa'     || 10

        Boolean | 'yes'     || true
        Boolean | 'on'      || true
        Boolean | 'true'    || true
        Boolean | 'fsajl'   || false

        File    | 'a/b'     || new File("a${File.separator}/b")
        Path    | 'a/b'     || Paths.get("a${File.separator}/b")

        MyEnum  | 'foo'     || FOO
        MyEnum  | 'foo-bar' || FOO_BAR
    }

    @Unroll
    def "EnumSetOption correctly modifies the current EnumSet (#arg)"() {
      when:
        def enumSet = toEnumSet(initial)
        StandardArgumentConverters.forType(Arguments.multi.getGenericParameterTypes()[0]).convert(arg).applyTo(enumSet)

      then:
        enumSet == toEnumSet(result)

      where:
        initial | arg            || result

        [BAR]   | 'foo'          || [FOO, BAR]
        []      | '+foo'         || [FOO]
        [BAR]   | '-bar'         || []
        [BAR]   | '-bar,foo-bar' || [FOO_BAR]

    }

    @Unroll
    def "Illegal enum types don't return null (#type)"() {
      expect:
        StandardArgumentConverters.forType(type) == null

      where:
        type << [Arguments.illegalSimple.getGenericParameterTypes()[0],
                 Arguments.illegalMulti.getGenericParameterTypes()[0]]
    }

    private static EnumSet<MyEnum> toEnumSet(Collection<MyEnum> initial) {
        initial ? EnumSet.copyOf(initial) : EnumSet.noneOf(MyEnum)
    }

}
