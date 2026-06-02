package su.afk.l4d2.presenter.faq

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.faq
import kotlinproject.composeapp.generated.resources.faqAnswer1
import kotlinproject.composeapp.generated.resources.faqAnswer2
import kotlinproject.composeapp.generated.resources.faqAnswer3
import kotlinproject.composeapp.generated.resources.faqAnswer4
import kotlinproject.composeapp.generated.resources.faqAnswer5
import kotlinproject.composeapp.generated.resources.faqAnswer6
import kotlinproject.composeapp.generated.resources.faqAnswer7
import kotlinproject.composeapp.generated.resources.faqAnswer8
import kotlinproject.composeapp.generated.resources.faqAnswer9
import kotlinproject.composeapp.generated.resources.faqQuestion1
import kotlinproject.composeapp.generated.resources.faqQuestion2
import kotlinproject.composeapp.generated.resources.faqQuestion3
import kotlinproject.composeapp.generated.resources.faqQuestion4
import kotlinproject.composeapp.generated.resources.faqQuestion5
import kotlinproject.composeapp.generated.resources.faqQuestion6
import kotlinproject.composeapp.generated.resources.faqQuestion7
import kotlinproject.composeapp.generated.resources.faqQuestion8
import kotlinproject.composeapp.generated.resources.faqQuestion9
import org.jetbrains.compose.resources.stringResource

@Composable
fun FAQScreen() {
    val faqList = listOf(
        FAQItem(
            question = stringResource(Res.string.faqQuestion1),
            answer = stringResource(Res.string.faqAnswer1)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion2),
            answer = stringResource(Res.string.faqAnswer2)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion3),
            answer = stringResource(Res.string.faqAnswer3)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion4),
            answer = stringResource(Res.string.faqAnswer4)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion5),
            answer = stringResource(Res.string.faqAnswer5)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion6),
            answer = stringResource(Res.string.faqAnswer6)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion7),
            answer = stringResource(Res.string.faqAnswer7)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion8),
            answer = stringResource(Res.string.faqAnswer8)
        ),
        FAQItem(
            question = stringResource(Res.string.faqQuestion9),
            answer = stringResource(Res.string.faqAnswer9)
        ),
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = stringResource(Res.string.faq),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(faqList) { faqItem ->
                FAQItemView(faqItem = faqItem)
            }
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

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faqItem.question,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    Text(
                        text = faqItem.answer,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
