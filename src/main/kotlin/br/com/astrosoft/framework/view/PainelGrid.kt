package br.com.astrosoft.framework.view

import br.com.astrosoft.transferenciaBancaria.view.main.FilterBar
import br.com.astrosoft.transferenciaBancaria.viewmodel.ITransferenciaBancariaView
import com.github.mvysny.karibudsl.v10.grid
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant.*
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.ListDataProvider

abstract class PainelGrid<T>(val view: ITransferenciaBancariaView, val blockUpdate: () -> Unit) : VerticalLayout() {
  private val provider = ListDataProvider<T>(mutableListOf())
  private var grid: Grid<T>? = null

  val filterBar: FilterBar by lazy {
    filterBar()
  }

  fun createGrid(): Grid<T> {
    return Grid<T>().apply {
      this.dataProvider = provider
      this.gridConfig()
    }
  }

  init {
    this.setSizeFull()
    isMargin = false
    isPadding = false
    initComponents()
  }

  private fun initComponents() {
    add(filterBar)
    if(grid == null) {
      grid = createGrid()
    }
    addAndExpand(grid)
  }

  protected abstract fun filterBar(): FilterBar

  fun updateGrid(itens: List<T>) {
    grid?.deselectAll()
    provider.updateItens(itens)
  }

  protected abstract fun Grid<T>.gridConfig()

  fun singleSelect(): T? = grid?.asSingleSelect()?.value
  fun multiSelect() = grid?.asMultiSelect()?.value?.toList().orEmpty()
}

