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

package fr.cnrs.liris.accio.lib.profiler.output

import com.twitter.util.Duration

object PhaseUtils {
  /**
   * Represents a double value as either "N/A" if it is NaN, or as a percentage with "%.2f%%".
   *
   * @param relativeValue is assumed to be a ratio of two values and will be multiplied with 100
   *                      for output
   */
  def prettyPercentage(relativeValue: Double): String = {
    if (relativeValue.isNaN) {
      "N/A"
    } else {
      "%.2f%%".format(relativeValue * 100)
    }
  }

  /**
   * Converts time to the user-friendly string representation.
   *
   * @param duration The length of time
   */
  def prettyTime(duration: Duration): String = {
    val ms = duration.inNanoseconds / 1000000d
    if (ms < 10.0) {
      f"$ms%.2f ms"
    } else if (ms < 100.0) {
      f"$ms%.1f ms"
    } else if (ms < 1000.0) {
      f"$ms%.0f ms"
    } else {
      f"${ms / 1000.0}%.3f s"
    }
  }
}
