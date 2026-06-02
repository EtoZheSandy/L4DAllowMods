import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.imageio.ImageReadParam
import kotlin.math.ceil
import kotlin.math.max

fun loadImageBitmap(
    file: File,
    maxWidth: Int? = null,
    maxHeight: Int? = null
): ImageBitmap {
    val bufferedImage = readBufferedImage(file, maxWidth, maxHeight)
    return bufferedImage.toComposeImageBitmap()
}

private fun readBufferedImage(
    file: File,
    maxWidth: Int?,
    maxHeight: Int?
): BufferedImage {
    if (maxWidth == null || maxHeight == null) {
        return ImageIO.read(file)
    }

    val readers = ImageIO.getImageReadersBySuffix(file.extension)
    if (!readers.hasNext()) {
        return scaleToFit(ImageIO.read(file), maxWidth, maxHeight)
    }

    val reader = readers.next()
    try {
        return ImageIO.createImageInputStream(file).use { input ->
            reader.input = input
            val width = reader.getWidth(0)
            val height = reader.getHeight(0)
            val subsampling = calculateSubsampling(width, height, maxWidth, maxHeight)
            val params = reader.defaultReadParam.applySubsampling(subsampling)
            val sampledImage = reader.read(0, params)
            scaleToFit(sampledImage, maxWidth, maxHeight)
        }
    } finally {
        reader.dispose()
    }
}

private fun ImageReadParam.applySubsampling(subsampling: Int): ImageReadParam {
    if (subsampling > 1) {
        setSourceSubsampling(subsampling, subsampling, 0, 0)
    }
    return this
}

private fun calculateSubsampling(
    width: Int,
    height: Int,
    maxWidth: Int,
    maxHeight: Int
): Int {
    val widthRatio = ceil(width.toDouble() / maxWidth).toInt()
    val heightRatio = ceil(height.toDouble() / maxHeight).toInt()
    return max(1, max(widthRatio, heightRatio) / 2)
}

private fun scaleToFit(
    image: BufferedImage,
    maxWidth: Int,
    maxHeight: Int
): BufferedImage {
    if (image.width <= maxWidth && image.height <= maxHeight) {
        return image
    }

    val scale = minOf(
        maxWidth.toDouble() / image.width,
        maxHeight.toDouble() / image.height
    )
    val scaledWidth = max(1, (image.width * scale).toInt())
    val scaledHeight = max(1, (image.height * scale).toInt())

    val scaledImage = BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB)
    val graphics = scaledImage.createGraphics()
    try {
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.drawImage(
            image,
            0,
            0,
            scaledWidth,
            scaledHeight,
            null
        )
    } finally {
        graphics.dispose()
    }
    return scaledImage
}
