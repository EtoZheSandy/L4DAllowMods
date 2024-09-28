package su.afk.l4d2.presenter.faq

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun FAQScreen() {
    val faqList = listOf(
        FAQItem(
            question = "Сервер не пускает меня ошибка gameinfo.txt",
            answer = "Некоторые сервера проверяют этот файл на соответсвие стандартному, " +
                    "если он изменен он не пустит тебя на этот сервер"
        ),
        FAQItem(
            question = "Но как тогда игра с модами на таком сервер?",
            answer = "Перед запуском игры:\n" +
                    "1. Включаем Addons\n" +
                    "2. Заходим в главное меню игры (моды уже загружены на этом моменте)\n" +
                    "3. Отключаем Addons (файл gameinfo возвращен в исходное состояние) "
        ),
        FAQItem(
            question = "Не видит мои addons!",
            answer = "Они есть в папке addons\\workshop - если нету то подпишись в Steam на Addons и зайди в игру что бы они скачались"
        ),
        FAQItem(
            question = "Addons не работаю в игре",
            answer = "Значит ты забыл выбрать их и включить ПЕРЕД заходом в игре (это важно), попробуй еще раз"
        ),
        FAQItem(
            question = "Как работает выбор addons?",
            answer = "После указания папки с игрой tools ищет папку с названием addons/workshop\n" +
                    "И извлекает информацию о addons из .vpk файлах\n" +
                    "Если такой папки нету можете ее создать и поместить в нее .vpk файл"
        ),
        FAQItem(
            question = "Что такое кэш addons?",
            answer = "После выбора модов и нажатия 'Включить Addons'\n" +
                    "tools создает копии твоих .vpk файлов (кэш) что бы вшить их в игру\n" +
                    "При следующей загрузки игры они будут активированы\n" +
                    "Вы можете сбросить их кэш это удалит копии файлов в addons/workshop/id_Addons"
        ),
        FAQItem(
            question = "Как сбросить настройки tools",
            answer = "Если указали неверный путь до игровой папки или файл gameinfo был изменен и более не восстанавливается\n" +
                    "можно сбросить настройки tools в меню настроек и проверить кэш игры в Steam"
        ),
    )

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(faqList) { faqItem ->
            FAQItemView(faqItem = faqItem)
        }
    }
}


data class FAQItem(
    val question: String,
    val answer: String
)

@Composable
fun FAQItemView(faqItem: FAQItem) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = faqItem.question,
//                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.onSurface
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Свернуть" else "Развернуть"
            )
        }
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = faqItem.answer,
//                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}