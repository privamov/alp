package fr.cnrs.liris.util.flags

/**
 * The priority of flags values.
 *
 * In general, new values for flags can only override values with a lower or equal priority.
 * Flag values provided as default value in a flags class are implicitly at the
 * priority [[Priority.Default]].
 */

sealed class Priority(protected val level: Int) extends Ordered[Priority] {
  override def compare(that: Priority): Int = level.compareTo(that.level)
}

object Priority {

  /**
   * The priority of values specified in the field default value. This should never be specified
   * in calls to [[FlagsParser.parse]].
   */
  case object Default extends Priority(0)

  /**
   * Overrides default options at runtime, while still allowing the values to be
   * overridden manually.
   */
  case object ComputedDefault extends Priority(1)

  /**
   * For options coming from a configuration file or rc file.
   */
  case object RcFile extends Priority(2)

  /**
   * For options coming from the command line.
   */
  case object CommandLine extends Priority(3)

  /**
   * For options coming from invocation policy.
   */
  case object InvocationPolicy extends Priority(4)

  /**
   * This priority can be used to unconditionally override any user-provided options.
   * This should be used rarely and with caution!
   */
  case object SoftwareRequirement extends Priority(5)

}