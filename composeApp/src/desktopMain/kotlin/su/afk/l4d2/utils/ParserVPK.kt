package su.afk.l4d2.utils

import su.afk.l4d2.data.LogSystem
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

//  Функция для чтения .vpk файла и извлечения addoninfo.txt
fun parseVpkFile(vpkFile: File): String? {
    RandomAccessFile(vpkFile, "r").use { raf ->
        // Читаем сигнатуру
        val signature = raf.readIntLE()
        if (signature != 0x55AA1234) {
            LogSystem.addLog(1, "Неверная сигнатура VPK файла: ${vpkFile.name}")
            return null
        }

        // Читаем версию
        val version = raf.readIntLE()
        if (version != 1 && version != 2) {
            LogSystem.addLog(1, "Неподдерживаемая версия VPK файла: $version")
            return null
        }

        // Читаем размер дерева директорий
        val treeSize = raf.readIntLE()

        // Если версия 2, читаем дополнительные поля заголовка
        val headerSize = if (version == 2) {
            raf.readIntLE() // fileDataSectionSize
            raf.readIntLE() // archiveMD5SectionSize
            raf.readIntLE() // otherMD5SectionSize
            raf.readIntLE() // signatureSectionSize
            28 // размер заголовка для версии 2 (в байтах)
        } else {
            12 // размер заголовка для версии 1 (в байтах)
        }

        // Запоминаем позицию после заголовка
        val treeStart = raf.filePointer

        // Проходим по дереву директорий
        while (true) {
            val extension = raf.readNullTerminatedString()
            if (extension.isEmpty()) break

            while (true) {
                val path = raf.readNullTerminatedString()
                if (path.isEmpty()) break

                while (true) {
                    val filename = raf.readNullTerminatedString()
                    if (filename.isEmpty()) break

                    val fullPath = if (path == " ") {
                        "$filename.$extension"
                    } else {
                        "$path/$filename.$extension"
                    }

                    val crc32 = raf.readIntLE()
                    val preloadBytes = raf.readShortLE().toInt()
                    val archiveIndex = raf.readShortLE().toInt()
                    val entryOffset = raf.readIntLE()
                    val entryLength = raf.readIntLE()
                    val terminator = raf.readShortLE()

                    if (terminator != 0xFFFF.toShort()) {
                        LogSystem.addLog(1, "Неверный терминатор в VPK файле: ${vpkFile.name}")
                        return null
                    }

                    // Читаем прелоад-байты, если они есть
                    val preloadData = if (preloadBytes > 0) {
                        val data = ByteArray(preloadBytes)
                        raf.readFully(data)
                        data
                    } else {
                        ByteArray(0)
                    }

                    if (fullPath.equals("addoninfo.txt", ignoreCase = true)) {
                        // Если файл содержится в прелоад-данных
                        if (preloadData.isNotEmpty()) {
                            return String(preloadData, Charset.forName("UTF-8"))
                        } else {
                            // Читаем данные из файла
                            val data = ByteArray(entryLength)
                            val currentPos = raf.filePointer

                            // Вычисляем смещение данных
                            val dataOffset = entryOffset + treeSize + headerSize
                            raf.seek(dataOffset.toLong())
                            raf.readFully(data)
                            raf.seek(currentPos)
                            return String(data, Charset.forName("UTF-8"))
                        }
                    } else {
                        // Пропускаем прелоад-байты для других файлов
                        if (preloadBytes > 0) {
                            raf.skipBytes(preloadBytes)
                        }
                    }
                }
            }
        }
    }

    return null
}

/**
 * Функции readIntLE и readShortLE: Читают 4-байтовые и 2-байтовые числа в формате Little Endian.
 * Функция readNullTerminatedString: Читает строку, заканчивающуюся нулевым байтом.
 * */
fun RandomAccessFile.readIntLE(): Int {
    val bytes = ByteArray(4)
    this.readFully(bytes)
    return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).int
}

fun RandomAccessFile.readShortLE(): Short {
    val bytes = ByteArray(2)
    this.readFully(bytes)
    return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).short
}

fun RandomAccessFile.readNullTerminatedString(): String {
    val bytes = mutableListOf<Byte>()
    while (true) {
        val byte = this.readByte()
        if (byte.toInt() == 0) break
        bytes.add(byte)
    }
    return String(bytes.toByteArray(), Charset.forName("UTF-8"))
}
