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

package fr.cnrs.liris.privamov.model

import breeze.stats.DescriptiveStats
import com.google.common.base.MoreObjects
import fr.cnrs.liris.privamov.geo.{FeatureCollection, Point}
import fr.cnrs.liris.util.Distance

/**
 * A set of POIs belonging to a single user. This is essentially a wrapper around a basic set, providing some
 * useful methods to manipulate POIs.
 *
 * @param user A user
 * @param pois A set of POIs
 */
case class PoiSet(user: String, pois: Seq[Poi]) {
  /**
   * Check whether the set of POIs is not empty.
   *
   * @return True if there is at least one POI, false otherwise
   */
  def nonEmpty: Boolean = pois.nonEmpty

  /**
   * Check whether the set of POIs is empty.
   *
   * @return True if there is no POIs, false otherwise
   */
  def isEmpty: Boolean = pois.isEmpty

  /**
   * Return the number of POIs inside this set.
   *
   * @return The size of this set
   */
  def size: Int = pois.size

  /**
   * Compute the minimum distance between POIs inside this set and another POI.
   *
   * @param poi A POI
   * @return The minimum distance
   */
  def distance(poi: Poi): Distance = PoiSet.distance(pois, poi)

  /**
   * Compute the distance with another set of POIs (it is symmetrical).
   *
   * @param that Another set of POIs
   * @return The distance with the other set
   */
  def distance(that: PoiSet): Distance = PoiSet.distance(pois, that.pois)

  /**
   * Convert this model to GeoJSON.
   *
   * @return A GeoJSON FeatureCollection
   */
  def toGeoJson: FeatureCollection = FeatureCollection(pois.toSeq.map(_.toGeoJson))

  def merge(that: PoiSet): PoiSet = {
    require(that.user == user, s"Cannot merge two PoisSet's belonging to different users ($user and ${that.user})")
    new PoiSet(user, pois ++ that.pois)
  }

  override def toString: String =
    MoreObjects.toStringHelper(this)
      .add("user", user)
      .add("size", size)
      .toString
}

object PoiSet {
  def create(user: String, pois: Seq[Poi]): PoiSet = new PoiSet(user, pois)

  def apply(user: String, pois: Iterable[Poi]): PoiSet = new PoiSet(user, pois.toSeq.distinct)

  /**
   * Compute the minimum distance between POIs inside a list and another POI.
   *
   * @param pois A list of POIs
   * @param poi  A POI
   */
  def distance(pois: Iterable[Poi], poi: Poi): Distance =
    Point.nearest(poi.centroid, pois.map(_.centroid)).distance

  def distance(as: Iterable[Poi], bs: Iterable[Poi]): Distance = {
    val a = as.map(_.centroid)
    val b = bs.map(_.centroid)
    val d = distances(a, b) ++ distances(b, a)
    if (d.nonEmpty) {
      Distance.meters(DescriptiveStats.percentile(d, 0.5))
    } else {
      Distance.Infinity
    }
  }

  /**
   * Compute all distances between each point in `a` and the closest point in `b` (it is *not* symmetrical).
   *
   * @param a A first set of points
   * @param b A second set of points
   */
  private def distances(a: Iterable[Point], b: Iterable[Point]): Iterable[Double] =
    if (b.isEmpty) {
      Iterable.empty[Double]
    } else {
      a.map(point => Point.nearest(point, b).distance.meters).filterNot(_.isInfinite)
    }
}