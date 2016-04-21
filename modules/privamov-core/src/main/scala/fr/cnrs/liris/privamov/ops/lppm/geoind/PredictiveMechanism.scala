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

package fr.cnrs.liris.privamov.ops.lppm.geoind

import fr.cnrs.liris.accio.core.framework.Mapper
import fr.cnrs.liris.privamov.model.{Record, Track}
import fr.cnrs.liris.accio.lib.param.ParamMap

import scala.collection.mutable

/**
 * Konstantinos Chatzikokolakis, Catuscia Palamidessi and Marco Stronati. A Predictive Differentially-Private
 * Mechanism for Mobility Traces. In Proceedings of PETS'2014.
 *
 * @param budgetManager
 * @param predictionFn
 * @param testFn
 */
class PredictiveMechanism(budgetManager: BudgetManager, predictionFn: PredictionFunction, testFn: TestFunction)
    extends Mapper {
  override def map(paramMap: ParamMap, track: Track): Track = {
    val run = mutable.ListBuffer.empty[Step]
    track.records.foreach { record =>
      run += step(record, run.toSeq)
    }
    track.copy(records = run.map(_.reported))
  }

  private def step(secret: Record, run: Seq[Step]): Step = {
    val budget = budgetManager(run)
    val prediction = predictionFn(run)
    val easy = testFn(budget.threshold, budget.epsilonTest)(secret, prediction)
    val reported = if (!easy) {
      secret.copy(point = prediction)
    } else {
      secret.copy(point = Laplace.noise(budget.epsilonNoise, secret.point))
    }
    Step(reported, easy)
  }
}

private[privamov] case class Step(reported: Record, easy: Boolean) {
  def hard: Boolean = !easy
}