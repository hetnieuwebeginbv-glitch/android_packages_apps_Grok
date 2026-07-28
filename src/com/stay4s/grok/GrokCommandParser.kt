package com.stay4s.grok

/**
 * Parses raw @grok input into structured commands for the Parallel Brain.
 */
object GrokCommandParser {

    fun parse(rawInput: String): ParsedCommand {
        val trimmed = rawInput.trim().removePrefix("@grok").trim()

        val intent = when {
            trimmed.startsWith("do", ignoreCase = true) || 
            trimmed.startsWith("analyseer", ignoreCase = true) -> "action"
            trimmed.startsWith("remember") || trimmed.startsWith("covenant") -> "memory"
            else -> "query"
        }

        val keywords = trimmed.lowercase().split(" ", ".", ",", "?")
            .filter { it.length > 3 }
            .take(8)

        return ParsedCommand(
            raw = rawInput,
            intent = intent,
            keywords = keywords,
            originalText = trimmed
        )
    }
}

data class ParsedCommand(
    val raw: String,
    val intent: String,
    val keywords: List<String>,
    val originalText: String
)
