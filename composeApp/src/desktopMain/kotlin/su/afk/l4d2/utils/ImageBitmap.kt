import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.io.File
import javax.imageio.ImageIO

fun loadImageBitmap(file: File): ImageBitmap {
    val bufferedImage = ImageIO.read(file)
    return bufferedImage.toComposeImageBitmap()
}
