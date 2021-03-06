package org.scalaide.core.text

import org.eclipse.jface.text.BadLocationException
import org.eclipse.jface.text.{Document => EDocument}
import org.junit.ComparisonFailure
import org.junit.Test
import org.scalaide.core.internal.text.TextDocument
import org.scalaide.util.eclipse.RegionUtils._

class DocumentTest {

  type Op = Document => String

  final def document(text: String): Document =
    new TextDocument(new EDocument(text))

  final implicit class Assert_===[A](actual: A) {
    def ===(expected: A): Unit =
      if (actual != expected)
        throw new ComparisonFailure("", expected.toString(), actual.toString())
  }

  final implicit class StringAsTest(input: String) {
    def becomes(expectedOutput: String) =
      document(input) -> expectedOutput
  }
  final implicit class TestExecutor(testData: (Document, String)) {
    def after(operation: Op) = test(testData._1, testData._2, operation)
  }

  final def test(doc: Document, expectedOutput: String, operation: Op): Unit = {
    val actual = operation(doc)
    actual === expectedOutput
  }

  @Test
  def length_returns_the_length() = {
    val d = document("hello world")
    d.length === 11
  }

  @Test
  def text_returns_string() =
    "hello world" becomes "hello world" after (_.text)

  @Test
  def textRange_returns_range() =
    "hello world" becomes "lo wo" after (_.textRange(3, 8))

  @Test(expected = classOf[BadLocationException])
  def textRange_throws_when_start_lower_than_zero() = {
    val d = document("hello world")
    d.textRange(-1, 8)
    ()
  }

  @Test(expected = classOf[BadLocationException])
  def textRange_throws_when_end_lower_than_start() = {
    val d = document("hello world")
    d.textRange(5, 4)
    ()
  }

  @Test(expected = classOf[BadLocationException])
  def textRange_throws_when_end_greater_than_length() = {
    val d = document("hello world")
    d.textRange(5, 20)
    ()
  }

  @Test
  def textRangeOpt_returns_option() = {
    val d = document("hello world")
    d.textRangeOpt(3, 8) === Some("lo wo")
  }

  @Test
  def textRangeOpt_returns_none_when_start_lower_than_zero() = {
    val d = document("hello world")
    d.textRangeOpt(-1, 8) === None
  }

  @Test
  def textRangeOpt_returns_none_when_end_lower_than_start() = {
    val d = document("hello world")
    d.textRangeOpt(5, 4) === None
  }

  @Test
  def textRangeOpt_returns_none_when_end_greater_than_length() = {
    val d = document("hello world")
    d.textRangeOpt(5, 20) === None
  }

  @Test
  def lines_returns_all_lines() = {
    val d = document("""|a
                        |bc
                        |def
                        |
                        |gh""".stripMargin)
    d.lines.map(r => (r.start, r.end)) === Seq(
        (0,1),
        (2,4),
        (5,8),
        (9,9),
        (10,12))
  }

  @Test
  def lineCount_returns_the_number_of_lines() = {
    val d = document("""|a
                        |bc
                        |def
                        |
                        |gh""".stripMargin)
    d.lineCount === 5
  }

  @Test
  def trim_trims_whitespace() =
    " \t  hello \t  " becomes "hello" after (d => d.lineInformation(0).trim(d).text(d))

  @Test
  def trim_trims_nothing_when_there_is_no_whitespace() =
    "hello" becomes "hello" after (d => d.lineInformation(0).trim(d).text(d))

  @Test
  def trimLeft_trims_left_whitespace() =
    " \t  hello \t  " becomes "hello \t  " after (d => d.lineInformation(0).trimLeft(d).text(d))

  @Test
  def trimLeft_trims_nothing_when_there_is_no_whitespace() =
    "hello \t  " becomes "hello \t  " after (d => d.lineInformation(0).trimLeft(d).text(d))

  @Test
  def trimRight_trims_right_whitespace() =
    " \t  hello \t  " becomes " \t  hello" after (d => d.lineInformation(0).trimRight(d).text(d))

  @Test
  def trimRight_trims_nothing_when_there_is_no_whitespace() =
    " \t  hello" becomes " \t  hello" after (d => d.lineInformation(0).trimRight(d).text(d))

  @Test
  def apply_on_non_empty_file_succeeds() =
    document("some text").apply(3) === 'e'

  @Test(expected = classOf[BadLocationException])
  def apply_thrown_when_index_lower_than_zero(): Unit =
    document("some text").apply(-1)

  @Test(expected = classOf[BadLocationException])
  def apply_thrown_when_index_greater_equal_than_length(): Unit =
    document("some text").apply(9)

  @Test
  def head_on_non_empty_file_succeeds() =
    document("some text").head === 's'

  @Test
  def headOpt_on_non_empty_file_succeeds() =
    document("some text").headOpt === Some('s')

  @Test
  def tail_on_non_empty_file_succeeds() =
    document("some text").tail === "ome text"

  @Test
  def tailOpt_on_non_empty_file_succeeds() =
    document("some text").tailOpt === Some("ome text")

  @Test
  def init_on_non_empty_file_succeeds() =
    document("some text").init === "some tex"

  @Test
  def initOpt_on_non_empty_file_succeeds() =
    document("some text").initOpt === Some("some tex")

  @Test
  def last_on_non_empty_file_succeeds() =
    document("some text").last === 't'

  @Test
  def lastOpt_on_non_empty_file_succeeds() =
    document("some text").lastOpt === Some('t')

  @Test(expected = classOf[BadLocationException])
  def head_on_empty_file_throws(): Unit =
    document("").head

  @Test
  def headOpt_on_empty_file_returns_none() =
    document("").headOpt === None

  @Test(expected = classOf[BadLocationException])
  def tail_on_empty_file_throws(): Unit =
    document("").tail

  @Test
  def tailOpt_on_empty_file_returns_none() =
    document("").tailOpt === None

  @Test(expected = classOf[BadLocationException])
  def init_on_empty_file_throws(): Unit =
    document("").init

  @Test
  def initOpt_on_empty_file_returns_none() =
    document("").initOpt === None

  @Test(expected = classOf[BadLocationException])
  def last_on_empty_file_throws(): Unit =
    document("").last

  @Test
  def lastOpt_on_empty_file_returns_none() =
    document("").lastOpt === None

  @Test
  def lineInformation_on_non_empty_file_succeeds() = {
    val d = document("""|first
                        |second
                        |third
                        |""".stripMargin)
    d.lineInformation(1).text(d) === "second"
  }

  @Test(expected = classOf[BadLocationException])
  def lineInformation_fails_for_line_number_gt_number_of_lines(): Unit = {
    val d = document("abc")
    d.lineInformation(2)
  }

  @Test(expected = classOf[BadLocationException])
  def lineInformation_fails_for_line_number_lt_zero(): Unit = {
    val d = document("abc")
    d.lineInformation(-1)
  }

  @Test
  def lineInformationOfOffset_on_non_empty_file_succeeds() = {
    val d = document("""|first
                        |second
                        |third
                        |""".stripMargin)
    d.lineInformationOfOffset(9).text(d) === "second"
  }

  @Test(expected = classOf[BadLocationException])
  def lineInformationOfOffset_fails_for_offset_gteq_length(): Unit = {
    val d = document("abc")
    d.lineInformationOfOffset(4)
  }

  @Test(expected = classOf[BadLocationException])
  def lineInformationOfOffset_fails_for_offset_lt_zero(): Unit = {
    val d = document("abc")
    d.lineInformationOfOffset(-1)
  }
}
