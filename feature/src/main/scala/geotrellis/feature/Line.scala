package geotrellis.feature

import com.vividsolutions.jts.{geom => jts}
import GeomFactory._

object Line {

  implicit def jtsToLine(jtsGeom: jts.LineString): Line =
    apply(jtsGeom)

  def apply(points: Point*): Line =
    apply(points.toList)

  def apply(points: Seq[Point])(implicit d: DummyImplicit): Line =
    apply(points.toList)

  def apply(points: Array[Point]): Line =
    apply(points.toList)

  def apply(points: List[Point]): Line = {
    if (points.length < 2) {
      sys.error("Invalid line: Requires 2 or more points.")
    }

    Line(factory.createLineString(points.map(_.jtsGeom.getCoordinate).toArray))
  }

}

case class Line(jtsGeom: jts.LineString) extends Geometry
                                         with Relatable
                                         with TwoDimensions {

  assert(!jtsGeom.isEmpty)

  lazy val points: List[Point] = jtsGeom.getCoordinates.map(c => Point(c.x, c.y)).toList

  lazy val isClosed: Boolean =
    jtsGeom.isClosed

  lazy val isSimple: Boolean =
    jtsGeom.isSimple

  lazy val boundary: OneDimensionBoundaryResult =
    jtsGeom.getBoundary

  lazy val vertices: MultiPoint =
    jtsGeom.getCoordinates

  lazy val boundingBox: Polygon =
    jtsGeom.getEnvelope match {
      case p: jts.Polygon => Polygon(p)
      case x =>
        sys.error(s"Unexpected result for Line boundingBox: ${x.getGeometryType}")
    }

  lazy val length: Double =
    jtsGeom.getLength

  // -- Intersection

  def &(p: Point): PointOrNoResult =
    intersection(p)
  def intersection(p: Point): PointOrNoResult =
    p.intersection(this)

  def &(g: AtLeastOneDimension): OneDimensionAtLeastOneDimensionIntersectionResult =
    intersection(g)
  def intersection(g: AtLeastOneDimension): OneDimensionAtLeastOneDimensionIntersectionResult =
    jtsGeom.intersection(g.jtsGeom)

  def &(mp: MultiPoint): MultiPointGeometryIntersectionResult =
    intersection(mp)
  def intersection(mp: MultiPoint): MultiPointGeometryIntersectionResult =
    jtsGeom.intersection(mp.jtsGeom)

  // -- Union

  def |(p: Point): PointLineUnionResult =
    union(p)
  def union(p: Point): PointLineUnionResult =
    p.union(this)

  def |(l: Line): LineLineUnionResult =
    union(l)
  def union(l: Line): LineLineUnionResult =
    jtsGeom.union(l.jtsGeom)

  def |(p: Polygon): AtMostOneDimensionPolygonUnionResult =
    union(p)
  def union(p: Polygon): AtMostOneDimensionPolygonUnionResult =
    jtsGeom.union(p.jtsGeom)

  def |(ps: MultiPoint): PointLineUnionResult =
    union(ps)
  def union(ps: MultiPoint): PointLineUnionResult =
    jtsGeom.union(ps.jtsGeom)

  def |(ls: MultiLine): LineLineUnionResult =
    union(ls)
  def union(ls: MultiLine): LineLineUnionResult =
    jtsGeom.union(ls.jtsGeom)

  def |(ps: MultiPolygon): AtMostOneDimensionMultiPolygonUnionResult =
    union(ps)
  def union(ps: MultiPolygon): AtMostOneDimensionMultiPolygonUnionResult =
    jtsGeom.union(ps.jtsGeom)

  // -- Difference

  def -(p: Point): LinePointDifferenceResult =
    difference(p)
  def difference(p: Point): LinePointDifferenceResult =
    jtsGeom.difference(p.jtsGeom)

  def -(l: Line): LineXDifferenceResult =
    difference(l)
  def difference(l: Line): LineXDifferenceResult =
    jtsGeom.difference(l.jtsGeom)

  def -(p: Polygon): LineXDifferenceResult =
    difference(p)
  def difference(p: Polygon): LineXDifferenceResult =
    jtsGeom.difference(p.jtsGeom)

  def -(ps: MultiPoint): LinePointDifferenceResult =
    difference(ps)
  def difference(ps: MultiPoint): LinePointDifferenceResult =
    jtsGeom.difference(ps.jtsGeom)

  def -(ls: MultiLine): LineXDifferenceResult =
    difference(ls)
  def difference(ls: MultiLine): LineXDifferenceResult =
    jtsGeom.difference(ls.jtsGeom)

  def -(ps: MultiPolygon): LineXDifferenceResult =
    difference(ps)
  def difference(ps: MultiPolygon): LineXDifferenceResult =
    jtsGeom.difference(ps.jtsGeom)

  // -- SymDifference

  def symDifference(g: ZeroDimensions): ZeroDimensionsLineSymDifferenceResult =
    jtsGeom.symDifference(g.jtsGeom)

  def symDifference(g: OneDimension): OneDimensionSymDifferenceResult =
    jtsGeom.symDifference(g.jtsGeom)

  def symDifference(p: Polygon): OneDimensionPolygonSymDifferenceResult =
    jtsGeom.symDifference(p.jtsGeom)
  
  def symDifference(ps: MultiPolygon): OneDimensionMultiPolygonSymDifferenceResult =
    jtsGeom.symDifference(ps.jtsGeom)

  // -- Buffer

  def buffer(d:Double):Polygon =
    jtsGeom.buffer(d).asInstanceOf[Polygon]

  // -- Predicates

  def contains(g: AtMostOneDimension): Boolean =
    jtsGeom.contains(g.jtsGeom)

  def coveredBy(g: AtLeastOneDimension): Boolean =
    jtsGeom.coveredBy(g.jtsGeom)

  def covers(g: AtMostOneDimension): Boolean =
    jtsGeom.covers(g.jtsGeom)

  def crosses(g: AtLeastOneDimension): Boolean =
    jtsGeom.crosses(g.jtsGeom)

  /** A Line crosses a MultiPoint when it covers
      some points but does not cover others */
  def crosses(ps: MultiPoint): Boolean =
    jtsGeom.crosses(ps.jtsGeom)

  def overlaps(g: OneDimension): Boolean =
    jtsGeom.overlaps(g.jtsGeom)

  def touches(g: Geometry): Boolean =
    jtsGeom.touches(g.jtsGeom)

  def within(g: AtLeastOneDimension): Boolean =
    jtsGeom.within(g.jtsGeom)

}
