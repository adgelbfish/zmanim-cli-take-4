import com.kosherjava.zmanim.ComplexZmanimCalendar
import com.kosherjava.zmanim.util.GeoLocation
import com.github.tototoshi.csv._

import java.io.{File, FileNotFoundException}
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.{Calendar, Date, Properties, TimeZone}
import com.typesafe.config.ConfigFactory


object ZmanimCli {
  def main(args: Array[String]) = {
    val myConfigFile = new File("application.conf")
    val fileConfig = ConfigFactory.parseFile(myConfigFile).getConfig("zmanim-app")
    val conf = ConfigFactory.load(fileConfig)

    val locationName = conf.getString("locationName")
    val latitude = conf.getDouble("latitude")
    val longitude = conf.getDouble("longitude")
    val elevation = conf.getDouble("elevation")
    val tz = conf.getString("tz")
    val dateStart = conf.getString("dateStart")
    val dateEnd = conf.getString("dateEnd")
    val useElevation = conf.getBoolean("useElevationAdjustmentForAllSunriseSunsetBasedZmanim")
    val timeZone = TimeZone.getTimeZone(tz)

    val location = new GeoLocation(locationName, latitude, longitude, elevation, timeZone)

    val dateFormat = "yyyy-MM-dd"
    val dtf = java.time.format.DateTimeFormatter.ofPattern(dateFormat)
    val start = java.time.LocalDate.parse(dateStart, dtf)
    val end = java.time.LocalDate.parse(dateEnd, dtf)
    val daysCount = ChronoUnit.DAYS.between(start, end).toInt

    val czc = new ComplexZmanimCalendar(location)
    czc.setUseElevation(useElevation)

    def escape(s: String): String = {
      return s.replace("\\\\", "-").replace("/", "-")
    }

    val string = (locationName + " -- " + start + " - " + end + " -- " + "lat- " + latitude + " -- long- " + longitude + " -- elev- " + elevation + " -- tz- " + tz)
    val filestring = escape(string) // ok
    val f = new File(filestring + ".csv")
    val writer = CSVWriter.open(f)
    val zmanimSelectionCsv = CSVReader.open(new File("zmanimSelection.csv"))
    val zmanimSelection = zmanimSelectionCsv.allWithHeaders().filter(_ ("Use") == "yes").map(_ ("Name"))
    writer.writeRow(getCsvLine(czc, start, zmanimSelection, true))
    (0 until daysCount).map(start.plusDays(_)).map(getCsvLine(czc, _, zmanimSelection)).foreach(writer.writeRow)

  }

  def getCsvLine(calendar: ComplexZmanimCalendar, date: LocalDate, zmanimSelection: List[String], headerOnly: Boolean = false): List[String] = {
    calendar.getCalendar.set(date.getYear, date.getMonthValue, date.getDayOfMonth)
    val outputFormat = new SimpleDateFormat("hh:mm:ss a")
    val zmanMap = Map(("ShaahZmanis19Point8Degrees" -> calendar.getShaahZmanis19Point8Degrees()),
      ("ShaahZmanis18Degrees" -> calendar.getShaahZmanis18Degrees()),
      ("ShaahZmanis26Degrees" -> calendar.getShaahZmanis26Degrees()),
      ("ShaahZmanis16Point1Degrees" -> calendar.getShaahZmanis16Point1Degrees()),
      ("ShaahZmanis60Minutes" -> calendar.getShaahZmanis60Minutes()),
      ("ShaahZmanis72Minutes" -> calendar.getShaahZmanis72Minutes()),
      ("ShaahZmanis72MinutesZmanis" -> calendar.getShaahZmanis72MinutesZmanis()),
      ("ShaahZmanis90Minutes" -> calendar.getShaahZmanis90Minutes()),
      ("ShaahZmanis90MinutesZmanis" -> calendar.getShaahZmanis90MinutesZmanis()),
      ("ShaahZmanis96MinutesZmanis" -> calendar.getShaahZmanis96MinutesZmanis()),
      ("ShaahZmanisAteretTorah" -> calendar.getShaahZmanisAteretTorah()),
      ("ShaahZmanis96Minutes" -> calendar.getShaahZmanis96Minutes()),
      ("ShaahZmanis120Minutes" -> calendar.getShaahZmanis120Minutes()),
      ("ShaahZmanis120MinutesZmanis" -> calendar.getShaahZmanis120MinutesZmanis()),
      ("PlagHamincha120MinutesZmanis" -> calendar.getPlagHamincha120MinutesZmanis()),
      ("PlagHamincha120Minutes" -> calendar.getPlagHamincha120Minutes()),
      ("Alos60" -> calendar.getAlos60()),
      ("Alos72Zmanis" -> calendar.getAlos72Zmanis()),
      ("Alos96" -> calendar.getAlos96()),
      ("Alos90Zmanis" -> calendar.getAlos90Zmanis()),
      ("Alos96Zmanis" -> calendar.getAlos96Zmanis()),
      ("Alos90" -> calendar.getAlos90()),
      ("Alos120" -> calendar.getAlos120()),
      ("Alos120Zmanis" -> calendar.getAlos120Zmanis()),
      ("Alos26Degrees" -> calendar.getAlos26Degrees()),
      ("Alos18Degrees" -> calendar.getAlos18Degrees()),
      ("Alos19Degrees" -> calendar.getAlos19Degrees()),
      ("Alos19Point8Degrees" -> calendar.getAlos19Point8Degrees()),
      ("Alos16Point1Degrees" -> calendar.getAlos16Point1Degrees()),
      ("Misheyakir11Point5Degrees" -> calendar.getMisheyakir11Point5Degrees()),
      ("Misheyakir11Degrees" -> calendar.getMisheyakir11Degrees()),
      ("Misheyakir10Point2Degrees" -> calendar.getMisheyakir10Point2Degrees()),
      ("Misheyakir7Point65Degrees" -> calendar.getMisheyakir7Point65Degrees()),
      ("Misheyakir9Point5Degrees" -> calendar.getMisheyakir9Point5Degrees()),
      ("SofZmanShmaMGA19Point8Degrees" -> calendar.getSofZmanShmaMGA19Point8Degrees()),
      ("SofZmanShmaMGA16Point1Degrees" -> calendar.getSofZmanShmaMGA16Point1Degrees()),
      ("SofZmanShmaMGA18Degrees" -> calendar.getSofZmanShmaMGA18Degrees()),
      ("SofZmanShmaMGA72Minutes" -> calendar.getSofZmanShmaMGA72Minutes()),
      ("SofZmanShmaMGA72MinutesZmanis" -> calendar.getSofZmanShmaMGA72MinutesZmanis()),
      ("SofZmanShmaMGA90Minutes" -> calendar.getSofZmanShmaMGA90Minutes()),
      ("SofZmanShmaMGA90MinutesZmanis" -> calendar.getSofZmanShmaMGA90MinutesZmanis()),
      ("SofZmanShmaMGA96Minutes" -> calendar.getSofZmanShmaMGA96Minutes()),
      ("SofZmanShmaMGA96MinutesZmanis" -> calendar.getSofZmanShmaMGA96MinutesZmanis()),
      ("SofZmanShma3HoursBeforeChatzos" -> calendar.getSofZmanShma3HoursBeforeChatzos()),
      ("SofZmanShmaMGA120Minutes" -> calendar.getSofZmanShmaMGA120Minutes()),
      ("SofZmanShmaAlos16Point1ToSunset" -> calendar.getSofZmanShmaAlos16Point1ToSunset()),
      ("SofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees" -> calendar.getSofZmanShmaAlos16Point1ToTzaisGeonim7Point083Degrees()),
      ("SofZmanShmaKolEliyahu" -> calendar.getSofZmanShmaKolEliyahu()),
      ("SofZmanTfilaMGA19Point8Degrees" -> calendar.getSofZmanTfilaMGA19Point8Degrees()),
      ("SofZmanTfilaMGA16Point1Degrees" -> calendar.getSofZmanTfilaMGA16Point1Degrees()),
      ("SofZmanTfilaMGA18Degrees" -> calendar.getSofZmanTfilaMGA18Degrees()),
      ("SofZmanTfilaMGA72Minutes" -> calendar.getSofZmanTfilaMGA72Minutes()),
      ("SofZmanTfilaMGA72MinutesZmanis" -> calendar.getSofZmanTfilaMGA72MinutesZmanis()),
      ("SofZmanTfilaMGA90Minutes" -> calendar.getSofZmanTfilaMGA90Minutes()),
      ("SofZmanTfilaMGA90MinutesZmanis" -> calendar.getSofZmanTfilaMGA90MinutesZmanis()),
      ("SofZmanTfilaMGA96Minutes" -> calendar.getSofZmanTfilaMGA96Minutes()),
      ("SofZmanTfilaMGA96MinutesZmanis" -> calendar.getSofZmanTfilaMGA96MinutesZmanis()),
      ("SofZmanTfilaMGA120Minutes" -> calendar.getSofZmanTfilaMGA120Minutes()),
      ("SofZmanTfila2HoursBeforeChatzos" -> calendar.getSofZmanTfila2HoursBeforeChatzos()),
      ("MinchaGedola30Minutes" -> calendar.getMinchaGedola30Minutes()),
      ("MinchaGedola72Minutes" -> calendar.getMinchaGedola72Minutes()),
      ("MinchaGedola16Point1Degrees" -> calendar.getMinchaGedola16Point1Degrees()),
      ("MinchaGedolaGreaterThan30" -> calendar.getMinchaGedolaGreaterThan30()),
      ("MinchaKetana16Point1Degrees" -> calendar.getMinchaKetana16Point1Degrees()),
      ("MinchaKetana72Minutes" -> calendar.getMinchaKetana72Minutes()),
      ("PlagHamincha60Minutes" -> calendar.getPlagHamincha60Minutes()),
      ("PlagHamincha72Minutes" -> calendar.getPlagHamincha72Minutes()),
      ("PlagHamincha90Minutes" -> calendar.getPlagHamincha90Minutes()),
      ("PlagHamincha96Minutes" -> calendar.getPlagHamincha96Minutes()),
      ("PlagHamincha96MinutesZmanis" -> calendar.getPlagHamincha96MinutesZmanis()),
      ("PlagHamincha90MinutesZmanis" -> calendar.getPlagHamincha90MinutesZmanis()),
      ("PlagHamincha72MinutesZmanis" -> calendar.getPlagHamincha72MinutesZmanis()),
      ("PlagHamincha16Point1Degrees" -> calendar.getPlagHamincha16Point1Degrees()),
      ("PlagHamincha19Point8Degrees" -> calendar.getPlagHamincha19Point8Degrees()),
      ("PlagHamincha26Degrees" -> calendar.getPlagHamincha26Degrees()),
      ("PlagHamincha18Degrees" -> calendar.getPlagHamincha18Degrees()),
      ("PlagAlosToSunset" -> calendar.getPlagAlosToSunset()),
      ("PlagAlos16Point1ToTzaisGeonim7Point083Degrees" -> calendar.getPlagAlos16Point1ToTzaisGeonim7Point083Degrees()),
      ("BainHasmashosRT13Point24Degrees" -> calendar.getBainHasmashosRT13Point24Degrees()),
      ("BainHasmashosRT58Point5Minutes" -> calendar.getBainHasmashosRT58Point5Minutes()),
      ("BainHasmashosRT13Point5MinutesBefore7Point083Degrees" -> calendar.getBainHasmashosRT13Point5MinutesBefore7Point083Degrees()),
      ("BainHasmashosRT2Stars" -> calendar.getBainHasmashosRT2Stars()),
      ("BainHasmashosYereim18Minutes" -> calendar.getBainHasmashosYereim18Minutes()),
      ("BainHasmashosYereim3Point05Degrees" -> calendar.getBainHasmashosYereim3Point05Degrees()),
      ("BainHasmashosYereim16Point875Minutes" -> calendar.getBainHasmashosYereim16Point875Minutes()),
      ("BainHasmashosYereim2Point8Degrees" -> calendar.getBainHasmashosYereim2Point8Degrees()),
      ("BainHasmashosYereim13Point5Minutes" -> calendar.getBainHasmashosYereim13Point5Minutes()),
      ("BainHasmashosYereim2Point1Degrees" -> calendar.getBainHasmashosYereim2Point1Degrees()),
      ("TzaisGeonim3Point7Degrees" -> calendar.getTzaisGeonim3Point7Degrees()),
      ("TzaisGeonim3Point8Degrees" -> calendar.getTzaisGeonim3Point8Degrees()),
      ("TzaisGeonim5Point95Degrees" -> calendar.getTzaisGeonim5Point95Degrees()),
      ("TzaisGeonim3Point65Degrees" -> calendar.getTzaisGeonim3Point65Degrees()),
      ("TzaisGeonim3Point676Degrees" -> calendar.getTzaisGeonim3Point676Degrees()),
      ("TzaisGeonim4Point61Degrees" -> calendar.getTzaisGeonim4Point61Degrees()),
      ("TzaisGeonim4Point37Degrees" -> calendar.getTzaisGeonim4Point37Degrees()),
      ("TzaisGeonim5Point88Degrees" -> calendar.getTzaisGeonim5Point88Degrees()),
      ("TzaisGeonim4Point8Degrees" -> calendar.getTzaisGeonim4Point8Degrees()),
      ("TzaisGeonim6Point45Degrees" -> calendar.getTzaisGeonim6Point45Degrees()),
      ("TzaisGeonim7Point083Degrees" -> calendar.getTzaisGeonim7Point083Degrees()),
      ("TzaisGeonim7Point67Degrees" -> calendar.getTzaisGeonim7Point67Degrees()),
      ("TzaisGeonim8Point5Degrees" -> calendar.getTzaisGeonim8Point5Degrees()),
      ("TzaisGeonim9Point3Degrees" -> calendar.getTzaisGeonim9Point3Degrees()),
      ("TzaisGeonim9Point75Degrees" -> calendar.getTzaisGeonim9Point75Degrees()),
      ("Tzais60" -> calendar.getTzais60()),
      ("TzaisAteretTorah" -> calendar.getTzaisAteretTorah()),
      ("AteretTorahSunsetOffset" -> calendar.getAteretTorahSunsetOffset()),
      ("SofZmanShmaAteretTorah" -> calendar.getSofZmanShmaAteretTorah()),
      ("SofZmanTfilahAteretTorah" -> calendar.getSofZmanTfilahAteretTorah()),
      ("MinchaGedolaAteretTorah" -> calendar.getMinchaGedolaAteretTorah()),
      ("MinchaKetanaAteretTorah" -> calendar.getMinchaKetanaAteretTorah()),
      ("PlagHaminchaAteretTorah" -> calendar.getPlagHaminchaAteretTorah()),
      ("Tzais72Zmanis" -> calendar.getTzais72Zmanis()),
      ("Tzais90Zmanis" -> calendar.getTzais90Zmanis()),
      ("Tzais96Zmanis" -> calendar.getTzais96Zmanis()),
      ("Tzais90" -> calendar.getTzais90()),
      ("Tzais120" -> calendar.getTzais120()),
      ("Tzais120Zmanis" -> calendar.getTzais120Zmanis()),
      ("Tzais16Point1Degrees" -> calendar.getTzais16Point1Degrees()),
      ("Tzais26Degrees" -> calendar.getTzais26Degrees()),
      ("Tzais18Degrees" -> calendar.getTzais18Degrees()),
      ("Tzais19Point8Degrees" -> calendar.getTzais19Point8Degrees()),
      ("Tzais96" -> calendar.getTzais96()),
      ("FixedLocalChatzos" -> calendar.getFixedLocalChatzos()),
      ("SofZmanShmaFixedLocal" -> calendar.getSofZmanShmaFixedLocal()),
      ("SofZmanTfilaFixedLocal" -> calendar.getSofZmanTfilaFixedLocal()),
      ("SofZmanKidushLevanaBetweenMoldos" -> calendar.getSofZmanKidushLevanaBetweenMoldos()),
      ("SofZmanKidushLevana15Days" -> calendar.getSofZmanKidushLevana15Days()),
      ("TchilasZmanKidushLevana3Days" -> calendar.getTchilasZmanKidushLevana3Days()),
      ("ZmanMolad" -> calendar.getZmanMolad()),
      /*      ("MidnightLastNight" -> calendar.getMidnightLastNight() ),
            ("MidnightTonight" -> calendar.getMidnightTonight() ),*/
      ("TchilasZmanKidushLevana7Days" -> calendar.getTchilasZmanKidushLevana7Days()),
      ("SofZmanAchilasChametzGRA" -> calendar.getSofZmanAchilasChametzGRA()),
      ("SofZmanAchilasChametzMGA72Minutes" -> calendar.getSofZmanAchilasChametzMGA72Minutes()),
      ("SofZmanAchilasChametzMGA16Point1Degrees" -> calendar.getSofZmanAchilasChametzMGA16Point1Degrees()),
      ("SofZmanBiurChametzGRA" -> calendar.getSofZmanBiurChametzGRA()),
      ("SofZmanBiurChametzMGA72Minutes" -> calendar.getSofZmanBiurChametzMGA72Minutes()),
      ("SofZmanBiurChametzMGA16Point1Degrees" -> calendar.getSofZmanBiurChametzMGA16Point1Degrees()),
      ("SolarMidnight" -> calendar.getSolarMidnight()), /*
      ("SunriseBaalHatanya" -> calendar.getSunriseBaalHatanya() ),
      ("SunsetBaalHatanya" -> calendar.getSunsetBaalHatanya() ),*/
      ("ShaahZmanisBaalHatanya" -> calendar.getShaahZmanisBaalHatanya()),
      ("AlosBaalHatanya" -> calendar.getAlosBaalHatanya()),
      ("SofZmanShmaBaalHatanya" -> calendar.getSofZmanShmaBaalHatanya()),
      ("SofZmanTfilaBaalHatanya" -> calendar.getSofZmanTfilaBaalHatanya()),
      ("SofZmanAchilasChametzBaalHatanya" -> calendar.getSofZmanAchilasChametzBaalHatanya()),
      ("SofZmanBiurChametzBaalHatanya" -> calendar.getSofZmanBiurChametzBaalHatanya()),
      ("MinchaGedolaBaalHatanya" -> calendar.getMinchaGedolaBaalHatanya()),
      ("MinchaGedolaBaalHatanyaGreaterThan30" -> calendar.getMinchaGedolaBaalHatanyaGreaterThan30()),
      ("MinchaKetanaBaalHatanya" -> calendar.getMinchaKetanaBaalHatanya()),
      ("PlagHaminchaBaalHatanya" -> calendar.getPlagHaminchaBaalHatanya()),
      ("TzaisBaalHatanya" -> calendar.getTzaisBaalHatanya()),
      ("SofZmanShmaMGA18DegreesToFixedLocalChatzos" -> calendar.getSofZmanShmaMGA18DegreesToFixedLocalChatzos()),
      ("SofZmanShmaMGA16Point1DegreesToFixedLocalChatzos" -> calendar.getSofZmanShmaMGA16Point1DegreesToFixedLocalChatzos()),
      ("SofZmanShmaMGA90MinutesToFixedLocalChatzos" -> calendar.getSofZmanShmaMGA90MinutesToFixedLocalChatzos()),
      ("SofZmanShmaMGA72MinutesToFixedLocalChatzos" -> calendar.getSofZmanShmaMGA72MinutesToFixedLocalChatzos()),
      ("SofZmanShmaGRASunriseToFixedLocalChatzos" -> calendar.getSofZmanShmaGRASunriseToFixedLocalChatzos()),
      ("SofZmanTfilaGRASunriseToFixedLocalChatzos" -> calendar.getSofZmanTfilaGRASunriseToFixedLocalChatzos()),
      ("MinchaGedolaGRAFixedLocalChatzos30Minutes" -> calendar.getMinchaGedolaGRAFixedLocalChatzos30Minutes()),
      ("MinchaKetanaGRAFixedLocalChatzosToSunset" -> calendar.getMinchaKetanaGRAFixedLocalChatzosToSunset()),
      ("PlagHaminchaGRAFixedLocalChatzosToSunset" -> calendar.getPlagHaminchaGRAFixedLocalChatzosToSunset()),
      ("Tzais50" -> calendar.getTzais50()), /*
      ("SamuchLeMinchaKetanaGRA" -> calendar.getSamuchLeMinchaKetanaGRA() ),
      ("SamuchLeMinchaKetana16Point1Degrees" -> calendar.getSamuchLeMinchaKetana16Point1Degrees() ),
      ("SamuchLeMinchaKetana72Minutes" -> calendar.getSamuchLeMinchaKetana72Minutes() ),
      ("ElevationAdjustedSunrise" -> calendar.getElevationAdjustedSunrise() ),
      ("ElevationAdjustedSunset" -> calendar.getElevationAdjustedSunset() ),*/
      ("Sunrise", calendar.getSunrise()),
      ("SeaLevelSunrise", calendar.getSeaLevelSunrise()),
      ("Sunset", calendar.getSunset()),
      ("SeaLevelSunset", calendar.getSeaLevelSunset()),
      ("Tzais" -> calendar.getTzais()),
      ("AlosHashachar" -> calendar.getAlosHashachar()),
      ("Alos72" -> calendar.getAlos72()),
      ("Chatzos" -> calendar.getChatzos()),
      ("SofZmanShmaGRA" -> calendar.getSofZmanShmaGRA()),
      ("SofZmanShmaMGA" -> calendar.getSofZmanShmaMGA()),
      ("Tzais72" -> calendar.getTzais72()),
      ("CandleLighting" -> calendar.getCandleLighting()),
      ("SofZmanTfilaGRA" -> calendar.getSofZmanTfilaGRA()),
      ("SofZmanTfilaMGA" -> calendar.getSofZmanTfilaMGA()),
      ("MinchaGedola" -> calendar.getMinchaGedola()),
      ("MinchaKetana" -> calendar.getMinchaKetana()),
      ("PlagHamincha" -> calendar.getPlagHamincha()),
      ("ShaahZmanisGra" -> calendar.getShaahZmanisGra()),
      ("ShaahZmanisMGA" -> calendar.getShaahZmanisMGA()),
    )
    if (headerOnly) {
      return List("Date").concat(zmanimSelection)
    }
    val retval = List(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).concat(zmanimSelection.map(zmanMap.getOrElse(_, "unavailable")).map {
      case d: Date => outputFormat.format(d)
      case l: Long => l.toString
      case "unavailable" => "Currently Unavailable to Generate"
      case _ => ""
    }.toList)

    return retval
  }
}
