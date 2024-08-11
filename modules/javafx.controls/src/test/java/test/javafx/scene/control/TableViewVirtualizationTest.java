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

package test.javafx.scene.control;

import com.sun.javafx.scene.control.VirtualScrollBar;
import com.sun.javafx.tk.Toolkit;
import javafx.collections.FXCollections;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableViewVirtualizationTest {

    private StageLoader stageLoader;
    private TableView<String> tableView;

    @BeforeEach
    void setUp() {
        tableView = new TableView<>();
        tableView.setFixedCellSize(24);
        tableView.setPrefWidth(300);
        tableView.setItems(FXCollections.observableArrayList("1", "2", "3", "4"));

        for (int index = 0; index < 5; index++) {
            TableColumn<String, String> tableColumn = new TableColumn<>(String.valueOf(index));
            tableColumn.setPrefWidth(100);
            tableView.getColumns().add(tableColumn);
        }

        stageLoader = new StageLoader(tableView);

        Toolkit.getToolkit().firePulse();
    }

    @AfterEach
    void cleanUp() {
        if (stageLoader != null) {
            stageLoader.dispose();
        }
    }

    @Test
    void testHorizontalVirtualizationInitial() {
        for (int index = 0; index < tableView.getItems().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(tableView, index);

            assertEquals(3, row.getChildrenUnmodifiable().size());
        }
    }

    @Test
    void testHorizontalVirtualizationInitialChangeFixedCellSize() {
        for (int index = 0; index < tableView.getItems().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(tableView, index);

            assertEquals(3, row.getChildrenUnmodifiable().size());
        }

        tableView.setFixedCellSize(-1);
        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < tableView.getItems().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(tableView, index);

            assertEquals(5, row.getChildrenUnmodifiable().size());
        }

        tableView.setFixedCellSize(24);
        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < tableView.getItems().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(tableView, index);

            assertEquals(3, row.getChildrenUnmodifiable().size());
        }
    }

    @Test
    void testHorizontalVirtualizationScrolledToEnd() {
        VirtualScrollBar scrollBar = VirtualFlowTestUtils.getVirtualFlowHorizontalScrollbar(tableView);
        scrollBar.setValue(scrollBar.getMax());

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < tableView.getItems().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(tableView, index);

            assertEquals(3, row.getChildrenUnmodifiable().size());
        }
    }

    @Test
    void testHorizontalVirtualizationScrolledToEndAndStart() {
        VirtualScrollBar scrollBar = VirtualFlowTestUtils.getVirtualFlowHorizontalScrollbar(tableView);
        scrollBar.setValue(scrollBar.getMax());

        Toolkit.getToolkit().firePulse();

        scrollBar.setValue(0);

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < tableView.getItems().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(tableView, index);

            assertEquals(3, row.getChildrenUnmodifiable().size());
        }
    }

    @Test
    void testHorizontalVirtualizationScrollingWithinFourthColumn() {
        VirtualScrollBar scrollBar = VirtualFlowTestUtils.getVirtualFlowHorizontalScrollbar(tableView);
        scrollBar.setValue(scrollBar.getMax());

        Toolkit.getToolkit().firePulse();

        scrollBar.setValue(10);

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < tableView.getItems().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(tableView, index);

            assertEquals(4, row.getChildrenUnmodifiable().size());
        }
    }

    @Test
    void testHorizontalVirtualizationIncreaseTableSize() {
        tableView.setPrefWidth(400);

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < tableView.getItems().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(tableView, index);

            assertEquals(4, row.getChildrenUnmodifiable().size());
        }
    }

    @Test
    void testHorizontalVirtualizationDecreaseTableSize() {
        tableView.setPrefWidth(200);

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < tableView.getItems().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(tableView, index);

            assertEquals(2, row.getChildrenUnmodifiable().size());
        }
    }

    @Test
    void testHorizontalVirtualizationIncreaseColumnSize() {
        tableView.getColumns().getFirst().setPrefWidth(200);

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < tableView.getItems().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(tableView, index);

            assertEquals(2, row.getChildrenUnmodifiable().size());
        }
    }

    @Test
    void testHorizontalVirtualizationDecreaseColumnSize() {
        tableView.getColumns().getFirst().setPrefWidth(50);

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < tableView.getItems().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(tableView, index);

            assertEquals(4, row.getChildrenUnmodifiable().size());
        }
    }

}
