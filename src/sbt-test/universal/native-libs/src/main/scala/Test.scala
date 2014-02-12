import com.almworks.sqlite4java._
import java.io.File

object Test extends App {
  val db = new SQLiteConnection(new File("/tmp/database"))
  db.open(true)
  db.dispose()
}