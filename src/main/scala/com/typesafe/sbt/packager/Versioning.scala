package com.typesafe.sbt
package packager

import sbt.{Setting, SettingKey}
import sbt.Keys.version
import SbtNativePackager.{Windows, Linux, Rpm, Debian, Universal}
import Keys.rpmRelease

/** Provides helpful defaults for versioning.  Note:  These can easily get in the
 * way of a more useful versioning scheme, but provide a good basis for those who don't care,
 * and need to unify on a single versioning scheme.
 *
 *
 * TODO - This really should support a package version number in addition to the
 * unerlying packaged project version across all the canonical versions.
 */
object Versioning {

  /** Default version settings that attempt to use cannonical maven versions.
   *
   * Takes maven-style version like `2.1.0-RC1` and `2.10.0-M1` and creates
   * a packaging version.   Does *not* support SNAPSHOT versions.
   */
  def versionSettings(versionKey: SettingKey[String] = sbt.Keys.version): Seq[Setting[_]] = Seq(
    version in Universal <<= version apply canonicalVersion,
    version in Windows <<= version apply windowsVersion,
    version in Rpm <<= version apply rpmVersion,
    version in Debian <<= version apply debianVersion,
    rpmRelease <<= version apply rpmBuildNumber
  )

  /** Takes a release version and calculates the RedHat "build number" for
   * an RPM.  This denotes patches to an existing, released version.
   */
  def rpmBuildNumber(version: String): String =
    // TODO - More robust version
    canonicalVersion(version) split "\\." match {
      case Array(_,_,_, b) => b
      case _               => "1"  // Default to just build 1
    }
  /** Takes a release version and calculates the RedHat 3-number version. */
  def rpmVersion(version: String): String =
    canonicalVersion(version) split "\\." match {
      case Array(m,n,b,_*) => "%s.%s.%s" format (m,n,b)
      case _ => version
    }

  /** Takes a release version and calculates a Debian 4-number version.
   * Note: that the last number denotes a build-number, which is usually reserved
   * to represent packaging fixes, but in this case is used to denote RC/Milestone
   * status.
   */
  def debianVersion(version: String): String =
    canonicalVersion(version) split "\\." match {
      case Array(m,n,b,z) => "%s.%s.%s-%s" format (m,n,b,z)
      case _ => version
    }

  /** Takes a release version and calculates a Windows 4-number version.
   * Note: that the last number denotes easy upgradability in windows.
   */
  def windowsVersion(version: String): String =
    canonicalVersion(version)

  /** This is a complicated means to convert maven version numbers into monotonically increasing windows versions.
   *
   * It's quite hacky, but supports -RC, -M and build numbers for *release* version numbers.
   *
   * It allows for ~20 milestones, ~20 RCs between releases.
   *
   * It only makes a good default for those who don't care too much about versions, but
   * want something that works across the various deployment platforms.
   */
  def canonicalVersion(version: String): String = {
    val Majors = new scala.util.matching.Regex("(\\d+).(\\d+).(\\d+)(-.*)?")
    val Rcs = new scala.util.matching.Regex("(\\-\\d+)?\\-RC(\\d+)")
    val Milestones = new scala.util.matching.Regex("(\\-\\d+)?\\-M(\\d+)")
    val BuildNum = new scala.util.matching.Regex("\\-(\\d+)")

    def calculateNumberFour(buildNum: Int = 0, rc: Int = 0, milestone: Int = 0) =
      if(rc > 0 || milestone > 0) (buildNum)*400 + rc*20  + milestone
      else (buildNum+1)*400 + rc*20  + milestone

    version match {
      case Majors(major, minor, bugfix, rest) => Option(rest) getOrElse "" match {
        case Milestones(null, num)            => major + "." + minor + "." + bugfix + "." + calculateNumberFour(0,0,num.toInt)
        case Milestones(bnum, num)            => major + "." + minor + "." + bugfix + "." + calculateNumberFour(bnum.drop(1).toInt,0,num.toInt)
        case Rcs(null, num)                   => major + "." + minor + "." + bugfix + "." + calculateNumberFour(0,num.toInt,0)
        case Rcs(bnum, num)                   => major + "." + minor + "." + bugfix + "." + calculateNumberFour(bnum.drop(1).toInt,num.toInt,0)
        case BuildNum(bnum)                   => major + "." + minor + "." + bugfix + "." + calculateNumberFour(bnum.toInt,0,0)
        case _                                => major + "." + minor + "." + bugfix + "." + calculateNumberFour(0,0,0)
      }
      case x => x
    }
  }
}