/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;
import test.com.sun.javafx.scene.control.infrastructure.VirtualFlowTestUtils;
import test.com.sun.javafx.scene.control.test.Person;

import static org.junit.Assert.assertEquals;

public class TreeTableRowSkinTest {

    private TreeTableView<Person> treeTableView;
    private StageLoader stageLoader;

    @Before
    public void before() {
        treeTableView = new TreeTableView<>();

        TreeTableColumn<Person, String> firstNameCol = new TreeTableColumn<>("Firstname");
        firstNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("firstName"));
        TreeTableColumn<Person, String> lastNameCol = new TreeTableColumn<>("Lastname");
        lastNameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("lastName"));
        TreeTableColumn<Person, String> emailCol = new TreeTableColumn<>("Email");
        emailCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("email"));
        TreeTableColumn<Person, Integer> ageCol = new TreeTableColumn<>("Age");
        ageCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("age"));

        treeTableView.getColumns().addAll(firstNameCol, lastNameCol, emailCol, ageCol);

        ObservableList<TreeItem<Person>> items = FXCollections.observableArrayList(
                new TreeItem<>(new Person("firstName1", "lastName1", "email1@javafx.com", 1)),
                new TreeItem<>(new Person("firstName2", "lastName2", "email2@javafx.com", 2)),
                new TreeItem<>(new Person("firstName3", "lastName3", "email3@javafx.com", 3)),
                new TreeItem<>(new Person("firstName4", "lastName4", "email4@javafx.com", 4))
        );

        TreeItem<Person> root = new TreeItem<>();
        root.getChildren().addAll(items);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);

        stageLoader = new StageLoader(treeTableView);
    }

    @Test
    public void treeTableRowShouldHonorPadding() {
        IndexedCell cell = VirtualFlowTestUtils.getCell(treeTableView, 0);

        double minWidth = cell.minWidth(-1);
        double prefWidth = cell.prefWidth(-1);
        double maxWidth = cell.maxWidth(-1);
        double width = cell.getWidth();

        double minHeight = cell.minHeight(-1);
        double prefHeight = cell.prefHeight(-1);
        double maxHeight = cell.maxHeight(-1);
        double height = cell.getHeight();

        treeTableView.setRowFactory(treeTableView -> {
            TreeTableRow row = new TreeTableRow();
            row.setPadding(new Insets(10, 20, 30, 40));
            return row;
        });

        treeTableView.refresh();
        Toolkit.getToolkit().firePulse();

        cell = VirtualFlowTestUtils.getCell(treeTableView, 0);

        // Insets left 40 + right 20 = 60
        assertEquals(minWidth + 60, cell.minWidth(-1), 0);
        assertEquals(prefWidth + 60, cell.prefWidth(-1), 0);
        assertEquals(maxWidth + 60, cell.maxWidth(-1), 0);
        assertEquals(width + 60, cell.getWidth(), 0);

        // Insets top 10 + bottom 30 = 40
        assertEquals(minHeight + 40, cell.minHeight(-1), 0);
        assertEquals(prefHeight + 40, cell.prefHeight(-1), 0);
        assertEquals(maxHeight + 40, cell.maxHeight(-1), 0);
        assertEquals(height + 40, cell.getHeight(), 0);
    }

    @Test
    public void treeTableRowWithFixedCellSizeShouldIgnoreVerticalPadding() {
        treeTableView.setFixedCellSize(24);

        IndexedCell cell = VirtualFlowTestUtils.getCell(treeTableView, 0);

        double minWidth = cell.minWidth(-1);
        double prefWidth = cell.prefWidth(-1);
        double maxWidth = cell.maxWidth(-1);
        double width = cell.getWidth();

        double minHeight = cell.minHeight(-1);
        double prefHeight = cell.prefHeight(-1);
        double maxHeight = cell.maxHeight(-1);
        double height = cell.getHeight();

        treeTableView.setRowFactory(treeTableView -> {
            TreeTableRow row = new TreeTableRow();
            row.setPadding(new Insets(10, 20, 30, 40));
            return row;
        });

        treeTableView.refresh();
        Toolkit.getToolkit().firePulse();

        cell = VirtualFlowTestUtils.getCell(treeTableView, 0);

        // Insets left 40 + right 20 = 60
        assertEquals(minWidth + 60, cell.minWidth(-1), 0);
        assertEquals(prefWidth + 60, cell.prefWidth(-1), 0);
        assertEquals(maxWidth + 60, cell.maxWidth(-1), 0);
        assertEquals(width + 60, cell.getWidth(), 0);

        assertEquals(minHeight, cell.minHeight(-1), 0);
        assertEquals(prefHeight, cell.prefHeight(-1), 0);
        assertEquals(maxHeight, cell.maxHeight(-1), 0);
        assertEquals(height, cell.getHeight(), 0);
    }

    @Test
    public void treeTableRowWithCellSizeShouldHonorPadding() {
        IndexedCell cell = VirtualFlowTestUtils.getCell(treeTableView, 0);

        double minWidth = cell.minWidth(-1);
        double prefWidth = cell.prefWidth(-1);
        double maxWidth = cell.maxWidth(-1);
        double width = cell.getWidth();

        double minHeight = cell.minHeight(-1);
        double prefHeight = cell.prefHeight(-1);
        double maxHeight = cell.maxHeight(-1);
        double height = cell.getHeight();

        treeTableView.setRowFactory(treeTableView -> {
            TreeTableRow row = new TreeTableRow();
            row.setStyle("-fx-cell-size: 34px");
            row.setPadding(new Insets(10, 20, 30, 40));
            return row;
        });

        treeTableView.refresh();
        Toolkit.getToolkit().firePulse();

        cell = VirtualFlowTestUtils.getCell(treeTableView, 0);

        // Insets left 40 + right 20 = 60
        assertEquals(minWidth + 60, cell.minWidth(-1), 0);
        assertEquals(prefWidth + 60, cell.prefWidth(-1), 0);
        assertEquals(maxWidth + 60, cell.maxWidth(-1), 0);
        assertEquals(width + 60, cell.getWidth(), 0);

        // Insets top 10 + bottom 30 + increased cell size 10 = 50
        // minHeight will take the lowest height - which are the cells (24px)
        assertEquals(minHeight + 40, cell.minHeight(-1), 0);
        assertEquals(prefHeight + 50, cell.prefHeight(-1), 0);
        assertEquals(maxHeight + 50, cell.maxHeight(-1), 0);
        assertEquals(height + 50, cell.getHeight(), 0);
    }

    @Test
    public void removedColumnsShouldRemoveCorrespondingCellsInRowFixedCellSize() {
        treeTableView.setFixedCellSize(24);
        removedColumnsShouldRemoveCorrespondingCellsInRowImpl();
    }

    @Test
    public void removedColumnsShouldRemoveCorrespondingCellsInRow() {
        removedColumnsShouldRemoveCorrespondingCellsInRowImpl();
    }

    @Test
    public void invisibleColumnsShouldRemoveCorrespondingCellsInRowFixedCellSize() {
        treeTableView.setFixedCellSize(24);
        invisibleColumnsShouldRemoveCorrespondingCellsInRowImpl();
    }

    @Test
    public void invisibleColumnsShouldRemoveCorrespondingCellsInRow() {
        invisibleColumnsShouldRemoveCorrespondingCellsInRowImpl();
    }

    @After
    public void after() {
        stageLoader.dispose();
    }

    private void invisibleColumnsShouldRemoveCorrespondingCellsInRowImpl() {
        // Set the last 2 columns invisible.
        treeTableView.getColumns().get(treeTableView.getColumns().size() - 1).setVisible(false);
        treeTableView.getColumns().get(treeTableView.getColumns().size() - 2).setVisible(false);

        Toolkit.getToolkit().firePulse();

        // We set 2 columns to invisible, so the cell count should be decremented by 2 as well.
        // Note: TreeTableView has an additional children - the disclosure node.
        assertEquals(treeTableView.getColumns().size() - 1,
                VirtualFlowTestUtils.getCell(treeTableView, 0).getChildrenUnmodifiable().size());
    }

    private void removedColumnsShouldRemoveCorrespondingCellsInRowImpl() {
        // Remove the last 2 columns.
        treeTableView.getColumns().remove(treeTableView.getColumns().size() - 2, treeTableView.getColumns().size());

        Toolkit.getToolkit().firePulse();

        // We removed 2 columns, so the cell count should be decremented by 2 as well.
        // Note: TreeTableView has an additional children - the disclosure node.
        assertEquals(treeTableView.getColumns().size() + 1,
                VirtualFlowTestUtils.getCell(treeTableView, 0).getChildrenUnmodifiable().size());
    }

}
