/*
 * Copyright LIRIS-CNRS (2016)
 * Contributors: Vincent Primault <vincent.primault@liris.cnrs.fr>
 *
 * This software is a computer program whose purpose is to study location privacy.
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software. You can use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty and the software's author, the holder of the
 * economic rights, and the successive licensors have only limited liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading, using, modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean that it is complicated to manipulate, and that also
 * therefore means that it is reserved for developers and experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and, more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */

package fr.cnrs.liris.privamov.ops

import breeze.stats.DescriptiveStats._
import breeze.stats._
import fr.cnrs.liris.accio.core.framework.Metric

private[ops] object MetricUtils {
  def fscore(reference: Int, result: Int, matched: Int): Double = {
    val precision = this.precision(result, matched)
    val recall = this.recall(reference, matched)
    if (precision > 0 && recall > 0) {
      2 * precision * recall / (precision + recall)
    } else {
      0d
    }
  }

  def precision(result: Int, matched: Int): Double =
    if (result != 0) matched.toDouble / result else 0d

  def recall(reference: Int, matched: Int): Double =
    if (reference != 0) matched.toDouble / reference else 0d

  def informationRetrievalMetrics: Seq[String] = Seq("precision", "recall", "fscore")

  def informationRetrieval(reference: Int, result: Int, matched: Int): Seq[Metric] = {
    Seq(
      Metric("precision", precision(result, matched)),
      Metric("recall", recall(reference, matched)),
      Metric("fscore", fscore(reference, result, matched)))
  }

  def descriptiveStatsMetrics: Seq[String] = Seq("min", "max", "stddev", "avg", "median")

  def descriptiveStats(values: Seq[Double]): Seq[Metric] = {
    def maybe(fn: => Double) = if (values.nonEmpty) fn else 0d
    Seq(
      Metric("min", maybe(values.min)),
      Metric("max", maybe(values.max)),
      Metric("stddev", maybe(stddev(values))),
      Metric("avg", maybe(mean(values))),
      Metric("median", maybe(percentile(values, p = .5))))
  }
}