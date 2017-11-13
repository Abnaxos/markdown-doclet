package ch.raffael.mddoclet.core.options

import spock.lang.Specification


/**
 * @author Raffael Herzog
 */
class ReflectedOptionsSpec extends Specification {

    def "Simple string value"() {
      given:
        def args = new Arguments()

      when:
        ReflectedOptions.processorForMethod(args, Arguments.string).process('-string', ['foo bar'])

      then:
        args.stringValue == 'foo bar'
    }

    def "Simple enum value"() {
      given:
        def args = new Arguments()

      when:
        ReflectedOptions.processorForMethod(args, Arguments.simple).process('-enum', ['foo-bar'])

      then:
        args.enumValue == Arguments.MyEnum.FOO_BAR
    }

    def "Enum set value"() {
      given:
        def args = new Arguments()
        args.enumValues.add(Arguments.MyEnum.BAR)

      when:
        ReflectedOptions.processorForMethod(args, Arguments.multi).process('-string', ['+foo,-bar'])

      then:
        args.enumValues == EnumSet.of(Arguments.MyEnum.FOO)
    }

    def "Multi arguments option"() {
      given:
        def args = new Arguments()

      when:
        ReflectedOptions.processorForMethod(args, Arguments.multiArgs).process('-string', ['the string', 'foo-bar'])

      then:
        args.stringValue == 'the string'
        args.enumValue == Arguments.MyEnum.FOO_BAR
    }

}
