package br.com.astrosoft.framework.view

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.viewmodel.IView
import br.com.astrosoft.framework.viewmodel.ViewModel
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.charts.model.style.SolidColor
import com.vaadin.flow.component.datepicker.DatePicker
import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.ColumnTextAlign.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs.SelectedChangeEvent
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.renderer.LocalDateRenderer
import com.vaadin.flow.data.renderer.NumberRenderer
import com.vaadin.flow.data.renderer.TextRenderer
import com.vaadin.flow.router.*
import com.vaadin.flow.shared.Registration
import org.claspina.confirmdialog.ButtonOption.caption
import org.claspina.confirmdialog.ButtonOption.closeOnClick
import org.claspina.confirmdialog.ButtonType.OK
import org.claspina.confirmdialog.ConfirmDialog
import org.vaadin.olli.ClipboardHelper
import java.sql.Time
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*
import kotlin.reflect.KProperty1

abstract class ViewLayout<VM : ViewModel<*>> : VerticalLayout(), IView, BeforeLeaveObserver, BeforeEnterObserver,
  AfterNavigationObserver {
  abstract val viewModel: VM

  init {
    this.setSizeFull()
  }

  abstract fun isAccept(): Boolean

  override fun showError(msg: String) {
    ConfirmDialog.createError().withCaption("Erro do aplicativo").withMessage(msg).open()
  }

  override fun showWarning(msg: String) {
    ConfirmDialog.createWarning().withCaption("Aviso").withMessage(msg).open()
  }

  override fun showInformation(msg: String) {
    ConfirmDialog.createInfo().withCaption("Informação").withMessage(msg).open()
  }

  fun showForm(caption: String, form: FormLayout, runConfirm: (() -> Unit)) {
    ConfirmDialog.create().withCaption(caption).withMessage(form)
      .withButton(OK, Runnable { runConfirm() }, caption("Confirma"), closeOnClick(true))
      .withCancelButton(caption("Cancela")).open()
  }

  fun showQuestion(msg: String, execYes: () -> Unit) {
    showQuestion(msg, execYes, {})
  }

  private fun showQuestion(msg: String, execYes: () -> Unit, execNo: () -> Unit) {
    ConfirmDialog.createQuestion().withCaption("Confirmação").withMessage(msg).withYesButton(Runnable {
      execYes()
    }, caption("Sim")).withNoButton(Runnable { execNo() }, caption("Não")).open()
  }

  override fun beforeLeave(event: BeforeLeaveEvent?) {
  }

  override fun beforeEnter(event: BeforeEnterEvent?) {
  }

  override fun afterNavigation(event: AfterNavigationEvent?) {
  }

  fun VerticalLayout.form(title: String, componentes: KFormLayout.() -> Unit = {}) {
    formLayout {
      isExpand = true

      em(title) {
        colspan = 2
      }
      componentes()
    }
  }

  fun HasComponents.toolbar(compnentes: HorizontalLayout.() -> Unit) {
    this.horizontalLayout {
      width = "100%"
      compnentes()
    }
  }
}

fun (@VaadinDsl TabSheet).selectedChange(onEvent: (event: SelectedChangeEvent) -> Unit) {
  addSelectedChangeListener(ComponentEventListener<SelectedChangeEvent> { event -> onEvent(event) })
}

fun <T : Any> (@VaadinDsl Grid<T>).addColumnString(
  property: KProperty1<T, String?>, block: (@VaadinDsl Grid.Column<T>).() -> Unit = {}
): Grid.Column<T> {
  return this.addColumnFor(property) {
    this.isAutoWidth = true
    this.isResizable = true
    if (this.key == null) this.key = property.name
    this.left()
    this.block()
  }
}

fun <T> (@VaadinDsl Grid<T>).addColumnBool(
  property: KProperty1<T, Boolean?>, block: (@VaadinDsl Grid.Column<T>).() -> Unit = {}
): Grid.Column<T> {
  val column = this.addComponentColumn { bean ->
    val boleanValue = property.get(bean) ?: false
    if (boleanValue) VaadinIcon.CHECK_CIRCLE_O.create()
    else VaadinIcon.CIRCLE_THIN.create()
  }
  column.setComparator { o1, o2 ->
    val value1 = property.get(o1) ?: false
    val value2 = property.get(o2) ?: false
    value1.compareTo(value2)
  }
  column.width = "5em"
  column.center()
  column.block()
  return column
}

fun <T : Any> (@VaadinDsl Grid<T>).addColumnLocalDate(
  property: KProperty1<T, LocalDate?>,
  formatPattern: String = "dd/MM/yyyy",
  block: (@VaadinDsl Grid.Column<T>).() -> Unit = {}
): Grid.Column<T> {
  return this.addColumnFor(property, renderer = LocalDateRenderer(property, formatPattern)) {
    this.isAutoWidth = true
    this.isResizable = true
    if (this.key == null) this.key = property.name
    this.right()
    this.setComparator { a, b ->
      val dataA = property.get(a) ?: LocalDate.of(1900, 1, 1)
      val dataB = property.get(b) ?: LocalDate.of(1900, 1, 1)
      dataA.compareTo(dataB)
    }
    this.block()
  }
}

fun <T : Any> (@VaadinDsl Grid<T>).addColumnDate(
  property: KProperty1<T, Date?>, block: (@VaadinDsl Grid.Column<T>).() -> Unit = {}
): Grid.Column<T> {
  return this.addColumnFor(property, renderer = TextRenderer { bean ->
    val date = property.get(bean)
    date.format()
  }) {
    this.isAutoWidth = true
    if (this.key == null) this.key = property.name
    this.left()

    this.block()
  }
}

fun <T : Any> (@VaadinDsl Grid<T>).addColumnLocalTime(
  property: KProperty1<T, LocalTime?>, block: (@VaadinDsl Grid.Column<T>).() -> Unit = {}
): Grid.Column<T> {
  return this.addColumnFor(property, TextRenderer { bean ->
    val hora = property.get(bean)
    hora.format()
  }) {
    this.isAutoWidth = true
    if (this.key == null) this.key = property.name
    this.left()
    this.block()
  }
}

fun <T : Any> (@VaadinDsl Grid<T>).addColumnTime(
  property: KProperty1<T, Time?>, block: (@VaadinDsl Grid.Column<T>).() -> Unit = {}
): Grid.Column<T> {
  return this.addColumnFor(property, TextRenderer { bean ->
    val hora = property.get(bean)
    hora.format()
  }) {
    this.isAutoWidth = true
    if (this.key == null) this.key = property.name
    this.left()
    this.setComparator { a, b ->
      val dataA = property.get(a) ?: Time(0)
      val dataB = property.get(b) ?: Time(0)
      dataA.compareTo(dataB)
    }
    this.block()
  }
}

fun <T : Any> (@VaadinDsl Grid<T>).addColumnDouble(
  property: KProperty1<T, Double?>, pattern: String = "#,##0.00", block: (@VaadinDsl Grid.Column<T>).() -> Unit = {}
): Grid.Column<T> {
  return this.addColumnFor(property, renderer = NumberRenderer(property, DecimalFormat(pattern))) {
    this.isAutoWidth = true
    this.setComparator { a, b ->
      val dataA = property.get(a) ?: Double.MIN_VALUE
      val dataB = property.get(b) ?: Double.MIN_VALUE
      dataA.compareTo(dataB)
    }
    if (this.key == null) this.key = property.name
    this.right()
    this.block()
  }
}

fun <T> (@VaadinDsl Grid<T>).addColumnButton(
  iconButton: VaadinIcon, execButton: (T) -> Unit = {}, block: (@VaadinDsl Grid.Column<T>).() -> Unit = {}
): Grid.Column<T> {
  return addComponentColumn { bean ->
    Icon(iconButton).apply {
      this.style.set("cursor", "pointer");
      onLeftClick {
        execButton(bean)
      }
    }
  }.apply {
    this.width = "4em"
    this.center()
    this.block()
  }
}

fun <T> (@VaadinDsl Grid<T>).addColumnButtonClipBoard(
  iconButton: VaadinIcon,
  execButton: (T) -> Unit = {},
  textToClipBoard: T.() -> String,
  block: (@VaadinDsl Grid.Column<T>).() -> Unit = {}
): Grid.Column<T> {
  return addComponentColumn { bean ->
    val icon = iconButton.create()
    icon.onLeftClick {
      execButton(bean)
    }
    ClipboardHelper(textToClipBoard(bean), icon)
  }.apply {
    this.width = "4em"
    this.center()
    this.block()
  }
}

fun <T : Any> (@VaadinDsl Grid<T>).addColumnInt(
  property: KProperty1<T, Int?>, block: (@VaadinDsl Grid.Column<T>).() -> Unit = {}
): Grid.Column<T> {
  return this.addColumnFor(property) {
    if (this.key == null) this.key = property.name
    this.isAutoWidth = true
    this.isResizable = true
    this.setComparator { a, b ->
      val dataA = property.get(a) ?: Int.MIN_VALUE
      val dataB = property.get(b) ?: Int.MIN_VALUE
      dataA.compareTo(dataB)
    }
    this.right()
    this.block()
  }
}

fun <T> (@VaadinDsl Grid.Column<T>).right() {
  this.textAlign = END
}

fun <T> (@VaadinDsl Grid.Column<T>).left() {
  this.textAlign = START
}

fun <T> (@VaadinDsl Grid.Column<T>).center() {
  this.textAlign = CENTER
}

fun Component.style(name: String, value: String) {
  this.element.style.set(name, value)
}

fun Component.background(color: SolidColor) {
  this.style("background", color.toString())
}

class TabClick(s: String?) : Tab(s) {
  @DomEvent("click")
  class ClickTabEvent(source: Tab?, fromClient: Boolean) : ComponentEvent<Tab?>(source, fromClient)

  fun addClickListener(listener: ComponentEventListener<ClickTabEvent?>?): Registration {
    return addListener(ClickTabEvent::class.java, listener)
  }
}

fun DatePicker.localePtBr() {
  this.locale = Locale("pt-br")
  this.i18n = DatePickerI18n().setWeek("semana").setCalendar("calendário").setClear("apagar").setToday("hoje")
    .setCancel("cancelar").setFirstDayOfWeek(1).setMonthNames(
      Arrays.asList(
        "janeiro",
        "fevereiro",
        "março",
        "abril",
        "maio",
        "junho",
        "julho",
        "agosto",
        "setembro",
        "outubro",
        "novembro",
        "dezembro"
      )
    ).setWeekdays(Arrays.asList("domingo", "segunda", "terça", "quarta", "quinta", "sexta", "sábado"))
    .setWeekdaysShort(Arrays.asList("dom", "seg", "ter", "qua", "qui", "sex", "sab"))
}

fun <T> ListDataProvider<T>.updateItens(itens: List<T>) {
  this.items.clear()
  this.items.addAll(itens.sortedBy { it.hashCode() })
  this.refreshAll()
}

fun <T> TabSheet.tabGrid(label: String, painelGrid: PainelGrid<T>) = tab {
  painelGrid
}.apply {
  val button = Button(label) {
    painelGrid.blockUpdate()
  }
  button.addThemeVariants(ButtonVariant.LUMO_SMALL)
  this.addComponentAsFirst(button)
}