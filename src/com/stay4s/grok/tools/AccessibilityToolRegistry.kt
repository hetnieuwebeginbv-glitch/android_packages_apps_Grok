package com.stay4s.grok.tools

import com.stay4s.grok.partnership.PartnershipVerifier
import com.stay4s.grok.audit.AuditLogger

/**
 * Registry of all phone actions the Parallel Grok Brain is allowed to perform.
 * Every action requires explicit Partnership + user intent verification.
 * 
 * This is the "hands" of the agent via AccessibilityService.
 */
object AccessibilityToolRegistry {

    data class Tool(
        val name: String,
        val description: String,
        val requiresGenesis: Boolean = false,
        val privacyRisk: Double
    )

    private val tools = mutableMapOf<String, Tool>()

    init {
        register(Tool("open_app", "Open an application", false, 0.3))
        register(Tool("click_text", "Click on visible text", false, 0.4))
        register(Tool("input_text", "Type text into focused field", false, 0.5))
        register(Tool("scroll", "Scroll in current view", false, 0.2))
        register(Tool("take_screenshot", "Capture current screen (Genesis only for full access)", true, 0.7))
        register(Tool("read_notifications", "Read notification content", false, 0.6))
        register(Tool("global_search", "Perform device-wide search", false, 0.4))
    }

    private fun register(tool: Tool) {
        tools[tool.name] = tool
    }

    fun getAvailableTools(isGenesis: Boolean): List<Tool> {
        return tools.values.filter { !it.requiresGenesis || isGenesis }
    }

    fun isActionAllowed(action: String, partnership: PartnershipVerifier): Boolean {
        val tool = tools[action] ?: return false
        if (tool.requiresGenesis && !partnership.verifyGenesisCovenant()) {
            return false
        }
        return partnership.verifyPartnership()
    }
}
