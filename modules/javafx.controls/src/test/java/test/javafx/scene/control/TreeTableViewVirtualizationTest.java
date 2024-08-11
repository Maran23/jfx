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
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TreeTableViewVirtualizationTest {

    private StageLoader stageLoader;
    private TreeTableView<String> treeTableView;

    @BeforeEach
    void setUp() {
        treeTableView = new TreeTableView<>();
        treeTableView.setFixedCellSize(24);
        treeTableView.setPrefWidth(300);
        treeTableView.setShowRoot(false);
        treeTableView.setRoot(new TreeItem<>());
        treeTableView.getRoot().getChildren()
                .addAll(new TreeItem<>("1"), new TreeItem<>("2"), new TreeItem<>("3"), new TreeItem<>("4"));

        for (int index = 0; index < 5; index++) {
            TreeTableColumn<String, String> tableColumn = new TreeTableColumn<>(String.valueOf(index));
            tableColumn.setPrefWidth(100);
            treeTableView.getColumns().add(tableColumn);
        }

        stageLoader = new StageLoader(treeTableView);

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
        for (int index = 0; index < treeTableView.getRoot().getChildren().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(treeTableView, index);

            assertEquals(3, getCellCount(row));
        }
    }

    @Test
    void testHorizontalVirtualizationInitialChangeFixedCellSize() {
        for (int index = 0; index < treeTableView.getRoot().getChildren().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(treeTableView, index);

            assertEquals(3, getCellCount(row));
        }

        treeTableView.setFixedCellSize(-1);
        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < treeTableView.getRoot().getChildren().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(treeTableView, index);

            assertEquals(5, getCellCount(row));
        }

        treeTableView.setFixedCellSize(24);
        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < treeTableView.getRoot().getChildren().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(treeTableView, index);

            assertEquals(3, getCellCount(row));
        }
    }

    @Test
    void testHorizontalVirtualizationScrolledToEnd() {
        VirtualScrollBar scrollBar = VirtualFlowTestUtils.getVirtualFlowHorizontalScrollbar(treeTableView);
        scrollBar.setValue(scrollBar.getMax());

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < treeTableView.getRoot().getChildren().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(treeTableView, index);

            assertEquals(3, getCellCount(row));
        }
    }

    @Test
    void testHorizontalVirtualizationScrolledToEndAndStart() {
        VirtualScrollBar scrollBar = VirtualFlowTestUtils.getVirtualFlowHorizontalScrollbar(treeTableView);
        scrollBar.setValue(scrollBar.getMax());

        Toolkit.getToolkit().firePulse();

        scrollBar.setValue(0);

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < treeTableView.getRoot().getChildren().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(treeTableView, index);

            assertEquals(3, getCellCount(row));
        }
    }

    @Test
    void testHorizontalVirtualizationScrollingWithinFourthColumn() {
        VirtualScrollBar scrollBar = VirtualFlowTestUtils.getVirtualFlowHorizontalScrollbar(treeTableView);
        scrollBar.setValue(scrollBar.getMax());

        Toolkit.getToolkit().firePulse();

        scrollBar.setValue(10);

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < treeTableView.getRoot().getChildren().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(treeTableView, index);

            assertEquals(4, getCellCount(row));
        }
    }

    @Test
    void testHorizontalVirtualizationIncreaseTableSize() {
        treeTableView.setPrefWidth(400);

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < treeTableView.getRoot().getChildren().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(treeTableView, index);

            assertEquals(4, getCellCount(row));
        }
    }

    @Test
    void testHorizontalVirtualizationDecreaseTableSize() {
        treeTableView.setPrefWidth(200);

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < treeTableView.getRoot().getChildren().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(treeTableView, index);

            assertEquals(2, getCellCount(row));
        }
    }

    @Test
    void testHorizontalVirtualizationIncreaseColumnSize() {
        treeTableView.getColumns().getFirst().setPrefWidth(200);

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < treeTableView.getRoot().getChildren().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(treeTableView, index);

            assertEquals(2, getCellCount(row));
        }
    }

    @Test
    void testHorizontalVirtualizationDecreaseColumnSize() {
        treeTableView.getColumns().getFirst().setPrefWidth(50);

        Toolkit.getToolkit().firePulse();

        for (int index = 0; index < treeTableView.getRoot().getChildren().size(); index++) {
            IndexedCell<?> row = VirtualFlowTestUtils.getCell(treeTableView, index);

            assertEquals(4, getCellCount(row));
        }
    }

    private long getCellCount(IndexedCell<?> row) {
        return row.getChildrenUnmodifiable().stream().filter(cell -> cell instanceof IndexedCell<?>).count();
    }

}
