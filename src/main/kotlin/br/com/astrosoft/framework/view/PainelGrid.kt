package br.com.astrosoft.framework.view

import br.com.astrosoft.transferenciaBancaria.view.main.FilterBar
import br.com.astrosoft.transferenciaBancaria.viewmodel.ITransferenciaBancariaView
import com.github.mvysny.karibudsl.v10.grid
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant.*
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.ListDataProvider

abstract class PainelGrid<T>(val view: ITransferenciaBancariaView, val blockUpdate: () -> Unit) : VerticalLayout() {
  private var grid: Grid<T>? = null
  private val dataProvider = ListDataProvider<T>(mutableListOf())
  val filterBar: FilterBar by lazy {
    filterBar()
  }

  init {
    this.setSizeFull()
    isMargin = false
    isPadding = false
  }

  fun addComponents() {
    this.removeAll()
    add(filterBar())
    grid = this.grid(dataProvider = dataProvider) {
      addThemeVariants(LUMO_COMPACT, LUMO_COLUMN_BORDERS, LUMO_ROW_STRIPES)
      this.gridConfig()
    }
  }

  protected abstract fun filterBar(): FilterBar

  fun updateGrid(itens: List<T>) {
    grid?.deselectAll()
    dataProvider.updateItens(itens)
  }

  protected abstract fun Grid<T>.gridConfig()

  fun singleSelect(): T? = grid?.asSingleSelect()?.value
  fun multiSelect() = grid?.asMultiSelect()?.value?.toList().orEmpty()
}

