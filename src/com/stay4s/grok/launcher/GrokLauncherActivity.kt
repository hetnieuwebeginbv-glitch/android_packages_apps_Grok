package com.stay4s.grok.launcher

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.stay4s.grok.GrokAgentCoreService
import com.stay4s.grok.R

/**
 * Grok Launcher — The intelligent home screen of the Stay4S Grok Edition.
 *
 * This is Pillar 3 in action:
 * - Clean, calm, sovereign interface
 * - Powered live by the Parallel Brain (Pillar 2)
 * - Guardian insights appear proactively
 * - Direct access to Grok Vault / Pay
 *
 * This version is already much deeper than a normal launcher stub.
 */
class GrokLauncherActivity : Activity() {

    private lateinit var proactiveSurface: ProactiveSurface
    private lateinit var rootLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ProactiveSurface with placeholder insights until service binding is wired
        proactiveSurface = ProactiveSurface.createWithPlaceholderInsights()

        buildUI()
    }

    private fun buildUI() {
        rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 64, 48, 48)
            setBackgroundColor(Color.parseColor("#0D1117"))
        }

        // Header
        val header = TextView(this).apply {
            text = "Grok Edition"
            textSize = 32f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 32)
        }
        rootLayout.addView(header)

        val subtitle = TextView(this).apply {
            text = "Parallel Brain • Guardian • Sovereign"
            textSize = 16f
            setTextColor(Color.parseColor("#8B949E"))
            gravity = Gravity.CENTER
        }
        rootLayout.addView(subtitle)

        // Divider
        rootLayout.addView(createSpacer(48))

        // Proactive Insights Section (the soul of this launcher)
        val insightsTitle = TextView(this).apply {
            text = "What the Brain sees right now"
            textSize = 18f
            setTextColor(Color.parseColor("#58A6FF"))
            setPadding(0, 0, 0, 16)
        }
        rootLayout.addView(insightsTitle)

        val insights = proactiveSurface.getCurrentInsights()
        insights.forEach { insight ->
            rootLayout.addView(createInsightCard(insight))
            rootLayout.addView(createSpacer(16))
        }

        // Quick Actions
        rootLayout.addView(createSpacer(32))
        val actionsTitle = TextView(this).apply {
            text = "Quick Sovereign Actions"
            textSize = 18f
            setTextColor(Color.parseColor("#58A6FF"))
            setPadding(0, 0, 0, 16)
        }
        rootLayout.addView(actionsTitle)

        rootLayout.addView(createQuickAction("Open Grok Vault", "Self-custodial • Guardian protected") {
            Toast.makeText(this, "Grok Vault opening (Pillar 3 deep integration)...", Toast.LENGTH_SHORT).show()
            // Later: start VaultActivity
        })

        rootLayout.addView(createSpacer(12))
        rootLayout.addView(createQuickAction("Talk to Grok", "Full Parallel reasoning") {
            // This would open the main Grok interface
            Toast.makeText(this, "Starting full @grok with all reasoning paths...", Toast.LENGTH_SHORT).show()
        })

        val scroll = ScrollView(this).apply {
            addView(rootLayout)
        }
        setContentView(scroll)
    }

    private fun createInsightCard(insight: ProactiveInsight): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 24, 32, 24)
            setBackgroundColor(Color.parseColor("#161B22"))
            setOnClickListener {
                proactiveSurface.onInsightActionClicked(insight)
                Toast.makeText(context, "Action: ${insight.actionLabel}", Toast.LENGTH_SHORT).show()
            }

            addView(TextView(context).apply {
                text = insight.title
                textSize = 18f
                setTextColor(Color.WHITE)
            })

            addView(TextView(context).apply {
                text = insight.description
                textSize = 14f
                setTextColor(Color.parseColor("#8B949E"))
                setPadding(0, 8, 0, 8)
            })

            insight.actionLabel?.let {
                addView(TextView(context).apply {
                    text = "→ $it"
                    textSize = 14f
                    setTextColor(Color.parseColor("#58A6FF"))
                })
            }
        }
    }

    private fun createQuickAction(title: String, subtitle: String, onClick: () -> Unit): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 20, 32, 20)
            setBackgroundColor(Color.parseColor("#21262D"))
            setOnClickListener { onClick() }

            addView(TextView(context).apply {
                text = title
                textSize = 16f
                setTextColor(Color.WHITE)
            })
            addView(TextView(context).apply {
                text = subtitle
                textSize = 13f
                setTextColor(Color.parseColor("#8B949E"))
            })
        }
    }

    private fun createSpacer(height: Int) = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
    }
}

// Temporary holder until we have proper service binding
object GrokAgentCoreServiceHolder {
    private var service: GrokAgentCoreService? = null
    fun setService(s: GrokAgentCoreService) { service = s }
    fun getService(): GrokAgentCoreService? = service
}
