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

package fr.cnrs.liris.privamov.lib.clustering

import java.time.Duration

import fr.cnrs.liris.accio.lib.profiler.AutoProfiler.profile
import fr.cnrs.liris.accio.lib.profiler.ProfilerTask
import fr.cnrs.liris.privamov.model.Record
import fr.cnrs.liris.util.Distance

import scala.collection.mutable

/**
 * Density-time clustering algorithm performing the extraction of stays. A stay
 * is defined by a minimal amount of time spend in the same place (defined by a
 * maximum distance between two points of the cluster).
 *
 * R. Hariharan and K. Toyama. Project Lachesis: parsing and modeling
 * location histories. Geographic Information Science, 2004.
 *
 * @param minDuration Minimum amount of time spent inside a stay
 * @param maxDiameter Maximum diameter of a stay (in meters)
 */
class DTClusterer(minDuration: Duration, maxDiameter: Distance) extends Clusterer {
  override def cluster(records: Seq[Record]): Seq[Cluster] =
    profile("DT-clusterer", ProfilerTask.Clustering) {
      val clusters = mutable.ListBuffer.empty[Cluster]
      val candidate = mutable.ListBuffer.empty[Record]
      records.foreach(doCluster(_, candidate, clusters))
      handleCandidate(candidate, clusters)
      clusters
    }

  /**
   * Recursive clustering routine.
   *
   * @param record    Current record
   * @param candidate Current list of tuples being a candidate stay
   * @param clusters  Current list of clusters
   */
  private def doCluster(record: Record, candidate: mutable.ListBuffer[Record], clusters: mutable.ListBuffer[Cluster]): Unit =
    if (candidate.isEmpty || isInDiameter(record, candidate)) {
      candidate += record
    } else if (handleCandidate(candidate, clusters)) {
      candidate += record
    } else {
      candidate.remove(0)
      doCluster(record, candidate, clusters)
    }

  /**
   * Check if a record can be added to a candidate cluster without breaking the
   * distance requirement.
   *
   * @param record    A record to tst
   * @param candidate A collection of tuples
   * @return True if the tuple can be safely added, false other
   */
  private def isInDiameter(record: Record, candidate: Seq[Record]) =
    candidate.forall(_.point.distance(record.point) <= maxDiameter)

  /**
   * Check if a cluster is valid w.r.t. the time threshold.
   *
   * @param candidate Current candidate cluster
   * @param clusters  Current list of clusters
   * @return True if the stay is valid, false otherwise
   */
  private def handleCandidate(candidate: mutable.ListBuffer[Record], clusters: mutable.ListBuffer[Cluster]) =
    if (candidate.size <= 1) {
      false
    } else if (Duration.between(candidate.head.time, candidate.last.time).compareTo(minDuration) < 0) {
      false
    } else {
      clusters += new Cluster(candidate.toSet)
      candidate.clear()
      true
    }
}