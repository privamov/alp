package fr.cnrs.liris.util

import fr.cnrs.liris.util.testing.UnitSpec

class TextUtilsSpec extends UnitSpec {
  "TextUtils::paragraphFill" should "wrap text into lines of fixed length" in {
    val str = TextUtils.paragraphFill("In sit amet vehicula lacus, ut dictum amet.", width = 10)
    str shouldBe "In sit \namet \nvehicula \nlacus, ut \ndictum \namet."
  }

  /*it should "wrap too-long text" in {
    val str = TextUtils.paragraphFill("Insitametvehicula lacus,utdictumamet.", width = 10)
    str shouldBe "Insitametvehicula \nlacus,utdictumamet."
  }*/

  it should "wrap an empty string into itself" in {
    val str = TextUtils.paragraphFill("", width = 10)
    str shouldBe ""
  }

  it should "wrap text and preserve line breaks" in {
    val str = TextUtils.paragraphFill("In sit amet vehicula lacus,\nut dictum amet.", width = 10)
    str shouldBe "In sit \namet \nvehicula \nlacus,\nut dictum \namet."
  }
}
