/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package test.javafx.scene.control.skin;

import com.sun.javafx.tk.Toolkit;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.control.skin.VirtualFlowShim;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import test.com.sun.javafx.scene.control.test.Person;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VirtualContainerTest {

    private StageLoader stageLoader;

    @AfterEach
    void afterEach() {
        if (stageLoader != null) {
            stageLoader.dispose();
        }
    }

    @Test
    void testTableViewRefresh() {
        ObservableList<Person> items = Person.persons();
        TableView<Person> tableView = new TableView<>(items);

        TableColumn<Person, String> col = new TableColumn<>("Column");
        col.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getFirstName()));
        tableView.getColumns().add(col);

        setupStage(tableView);

        VirtualFlow<?> virtualFlow = VirtualFlowTestUtils.getVirtualFlow(tableView);

        List<IndexedCell> cells = new ArrayList<>();
        int index = 0;
        for (Person item : items) {
            IndexedCell cell = getCell(virtualFlow, index);
            assertSame(item, cell.getItem());

            cells.add(cell);

            index++;
        }

        tableView.refresh();
        Toolkit.getToolkit().firePulse();

        index = 0;
        for (Person item : items) {
            IndexedCell cell = getCell(virtualFlow, index);
            assertSame(item, cell.getItem());

            // We expect that the cell is still somewhere in the virtual flow sheet.
            IndexedCell existingCell = cells.get(index);
            assertTrue(getCells(virtualFlow).contains(existingCell));

            index++;
        }
    }

    @Test
    void testTableViewRefresh2() {
        ObservableList<Person> items = Person.persons();
        TableView<Person> tableView = new TableView<>(items);

        TableColumn<Person, String> col = new TableColumn<>("Column");
        col.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getFirstName()));
        tableView.getColumns().add(col);

        setupStage(tableView);

        VirtualFlow<?> virtualFlow = VirtualFlowTestUtils.getVirtualFlow(tableView);

        // A plain change by a setter is not reflected in the row text.
        Person firstItem = items.get(0);
        firstItem.setFirstName("Something else");

        IndexedCell row = getCell(virtualFlow, 0);
        assertSame(firstItem, row.getItem());

        IndexedCell cell = getCell(virtualFlow, 0, 0);
        assertNotEquals(firstItem.getFirstName(), cell.getText());

        Toolkit.getToolkit().firePulse();

        // Still not reflected after a normal frame
        row = getCell(virtualFlow, 0);
        assertSame(firstItem, row.getItem());

        cell = getCell(virtualFlow, 0, 0);
        assertNotEquals(firstItem.getFirstName(), cell.getText());

        tableView.refresh();
        Toolkit.getToolkit().firePulse();

        // After calling refresh, it will be inside the row.
        row = getCell(virtualFlow, 0);
        assertSame(firstItem, row.getItem());

        cell = getCell(virtualFlow, 0, 0);
        assertEquals(firstItem.getFirstName(), cell.getText());
    }

    private IndexedCell getCell(VirtualFlow<?> virtualFlow, int index, int column) {
        return (IndexedCell) getCell(virtualFlow, index).getChildrenUnmodifiable().get(column);
    }

    @Test
    void testListViewRefresh() {
        ObservableList<Person> items = Person.persons();
        ListView<Person> listView = new ListView<>(items);

        setupStage(listView);

        VirtualFlow<?> virtualFlow = VirtualFlowTestUtils.getVirtualFlow(listView);

        List<IndexedCell> cells = new ArrayList<>();
        int index = 0;
        for (Person item : items) {
            IndexedCell cell = getCell(virtualFlow, index++);
            assertSame(item, cell.getItem());

            cells.add(cell);
        }

        listView.refresh();
        Toolkit.getToolkit().firePulse();

        index = 0;
        for (Person item : items) {
            IndexedCell cell = getCell(virtualFlow, index);
            assertSame(item, cell.getItem());

            // We expect that the cell is still somewhere in the virtual flow sheet.
            IndexedCell existingCell = cells.get(index);
            assertTrue(getCells(virtualFlow).contains(existingCell));

            index++;
        }
    }

    @Test
    void testTreeViewRefresh() {
        ObservableList<Person> items = Person.persons();
        TreeTableView<Person> treeView = new TreeTableView<>(new TreeItem<>());
        treeView.setShowRoot(false);
        treeView.getRoot().getChildren().addAll(items.stream().map(TreeItem::new).toList());

        TreeTableColumn<Person, String> col = new TreeTableColumn<>("Column");
        col.setCellValueFactory(cdf -> new SimpleStringProperty(cdf.getValue().getValue().getFirstName()));
        treeView.getColumns().add(col);

        setupStage(treeView);

        VirtualFlow<?> virtualFlow = VirtualFlowTestUtils.getVirtualFlow(treeView);

        List<IndexedCell> cells = new ArrayList<>();
        int index = 0;
        for (Person item : items) {
            IndexedCell cell = getCell(virtualFlow, index++);
            assertSame(item, cell.getItem());

            cells.add(cell);
        }

        treeView.refresh();
        Toolkit.getToolkit().firePulse();

        index = 0;
        for (Person item : items) {
            IndexedCell cell = getCell(virtualFlow, index);
            assertSame(item, cell.getItem());

            // We expect that the cell is still somewhere in the virtual flow sheet.
            IndexedCell existingCell = cells.get(index);
            assertTrue(getCells(virtualFlow).contains(existingCell));

            index++;
        }
    }

    @Test
    void testTreeTableViewRefresh() {
        ObservableList<Person> items = Person.persons();
        TreeView<Person> treeView = new TreeView<>(new TreeItem<>());
        treeView.setShowRoot(false);
        treeView.getRoot().getChildren().addAll(items.stream().map(TreeItem::new).toList());

        setupStage(treeView);

        VirtualFlow<?> virtualFlow = VirtualFlowTestUtils.getVirtualFlow(treeView);

        List<IndexedCell> cells = new ArrayList<>();
        int index = 0;
        for (Person item : items) {
            IndexedCell cell = getCell(virtualFlow, index++);
            assertSame(item, cell.getItem());

            cells.add(cell);
        }

        treeView.refresh();
        Toolkit.getToolkit().firePulse();

        index = 0;
        for (Person item : items) {
            IndexedCell cell = getCell(virtualFlow, index);
            assertSame(item, cell.getItem());

            // We expect that the cell is still somewhere in the virtual flow sheet.
            IndexedCell existingCell = cells.get(index);
            assertTrue(getCells(virtualFlow).contains(existingCell));

            index++;
        }
    }

    private IndexedCell getCell(VirtualFlow<?> virtualFlow, int index) {
        ObservableList<Node> sheetChildren = getCells(virtualFlow);
        return sheetChildren.stream().map(node -> (IndexedCell) node).filter(cell -> cell.getIndex() == index)
                .findFirst().orElseThrow();
    }

    private ObservableList<Node> getCells(VirtualFlow<?> virtualFlow) {
        ObservableList<Node> sheetChildren = VirtualFlowShim.getSheetChildren(virtualFlow);
        return sheetChildren;
    }

    private void setupStage(Control control) {
        stageLoader = new StageLoader(control);
        stageLoader.getStage().setWidth(300);
        stageLoader.getStage().setHeight(300);
    }

}
