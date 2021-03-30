/*
 * Copyright (c) 2011, 2021, Oracle and/or its affiliates. All rights reserved.
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

package test.robot.javafx.scene.tableview;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableHeaderRow;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.TableViewSkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import test.util.Util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TableViewMenuTest {

    private static CountDownLatch startupLatch;
    private static Robot robot;

    private static Scene scene;
    private static StackPane root;

    private static boolean showColumnPopupMenuCalled;

    private TableView<?> tableView;

    @Test
    public void testTableMenu() throws Exception {
        // 0. Open the popup menu, collect the table popup menu (ContextMenu) and verify it
        clickTableColumnMenu();
        Assert.assertTrue(showColumnPopupMenuCalled);

        ContextMenu contextMenu = Stage.getWindows().stream().filter(window -> window instanceof ContextMenu)
                .map(window -> (ContextMenu) window).findFirst().orElse(null);
        Assert.assertNotNull(contextMenu);
        Assert.assertEquals(tableView.getColumns().size(), contextMenu.getItems().size());

        // Check, if the whole content is the same.
        for (int index = 0; index < tableView.getColumns().size(); index++) {
            TableColumn<?, ?> tableColumn = tableView.getColumns().get(index);
            CheckMenuItem menuItem = (CheckMenuItem) contextMenu.getItems().get(index);

            Assert.assertEquals(tableColumn.getText(), menuItem.getText());
            Assert.assertEquals(tableColumn.isVisible(), menuItem.isSelected());
            Assert.assertEquals(tableColumn.visibleProperty().isBound(), menuItem.isDisable());
        }

        // 1. Check, that the disabled menu item can't be clicked/does nothing.
        // The first menu item is disabled, as the visibleProperty of the column is bound
        // (so the menu item is unable to change the visibility of the column)
        TableColumn column = tableView.getColumns().get(0);
        CheckMenuItem menuItem = (CheckMenuItem) contextMenu.getItems().get(0);
        Assert.assertTrue(column.isVisible());
        Assert.assertTrue(menuItem.isSelected());
        Assert.assertTrue(menuItem.isDisable());

        clickItem(0);
        // As it is disabled, nothing happens and the menu is still open
        Assert.assertTrue(column.isVisible());
        Assert.assertTrue(menuItem.isSelected());
        Assert.assertTrue(menuItem.isDisable());

        closeTableColumnMenu();

        // 2. Enable the first menu item (disabled, see 1.) by unbinding the visibleProperty of the column
        column.visibleProperty().unbind();

        checkMenuItem(contextMenu, 0, true);

        clickTableColumnMenu();
        clickItem(0);

        Assert.assertFalse(column.isVisible());
        checkMenuItem(contextMenu, 0, false);

        // 3. Set the invisible column visible
        column = tableView.getColumns().get(1);
        Assert.assertFalse(column.isVisible());
        checkMenuItem(contextMenu, 1, false);

        clickTableColumnMenu();
        clickItem(1);

        Assert.assertTrue(column.isVisible());
        checkMenuItem(contextMenu, 1, true);

        // 4. A column text change should also be visible in the menu
        column = tableView.getColumns().get(2);
        clickTableColumnMenu();

        menuItem = (CheckMenuItem) contextMenu.getItems().get(2);
        Assert.assertEquals(column.getText(), menuItem.getText());

        closeTableColumnMenu();
        column.setText("MyNewText");

        clickTableColumnMenu();

        menuItem = (CheckMenuItem) contextMenu.getItems().get(2);
        Assert.assertEquals(column.getText(), menuItem.getText());
        Assert.assertEquals("MyNewText", menuItem.getText());

        closeTableColumnMenu();

        // 5. Set a visible column invisible
        column = tableView.getColumns().get(3);
        Assert.assertTrue(column.isVisible());
        checkMenuItem(contextMenu, 3, true);

        clickTableColumnMenu();
        clickItem(3);

        Assert.assertFalse(column.isVisible());
        checkMenuItem(contextMenu, 3, false);
    }

    private void checkMenuItem(ContextMenu contextMenu, int children, boolean selected) throws InterruptedException {
        clickTableColumnMenu();

        CheckMenuItem menuItem = (CheckMenuItem) contextMenu.getItems().get(children);
        Assert.assertEquals(selected, menuItem.isSelected());
        Assert.assertFalse(menuItem.isDisable());

        closeTableColumnMenu();
    }

    private void closeTableColumnMenu() {
        Util.runAndWait(() -> {
            robot.keyPress(KeyCode.ESCAPE);
            robot.keyRelease(KeyCode.ESCAPE);
        });
    }

    @BeforeClass
    public static void initFX() throws Exception {
        startupLatch = new CountDownLatch(1);
        new Thread(() -> Application.launch(TestApp.class, (String[]) null)).start();
        Assert.assertTrue("Timeout waiting for FX runtime to start", startupLatch.await(15, TimeUnit.SECONDS));
    }

    @AfterClass
    public static void exitFX() {
        Platform.exit();
    }

    @Before
    public void setupUI() {
        Util.runAndWait(() -> {
            tableView = new TableView<>();
            tableView.setTableMenuButtonVisible(true);

            TableColumn disabled = new TableColumn<>("Bound");
            BooleanProperty property = new SimpleBooleanProperty(true);
            disabled.visibleProperty().bind(property);

            tableView.getColumns().add(disabled);

            TableColumn invisible = new TableColumn<>("Invisible");
            invisible.setVisible(false);

            tableView.getColumns().add(invisible);

            TableColumn text = new TableColumn<>("Text");
            text.setVisible(false);

            tableView.getColumns().add(text);

            for (int counter = 0; counter < 100; counter++) {
                tableView.getColumns().addAll(new TableColumn<>(String.valueOf(counter)));
            }

            tableView.setSkin(new CustomTableViewSkin(tableView));
            root.getChildren().add(tableView);
        });
    }

    private void clickTableColumnMenu() throws InterruptedException {
        Util.runAndWait(() -> {
            robot.mouseMove(scene.getWindow().getX() + scene.getWindow().getWidth() - 5, scene.getWindow().getY() + 5);
            mousePress();
        });
        Thread.sleep(500);
    }

    private void mousePress() {
        robot.mousePress(MouseButton.PRIMARY);
        robot.mouseRelease(MouseButton.PRIMARY);
    }

    private void clickItem(int menuItem) throws InterruptedException {
        int y = 15 + menuItem * 25;
        Util.runAndWait(() -> {
            robot.mouseMove(scene.getWindow().getX() + scene.getWindow().getWidth() - 5, y);
            mousePress();
        });
        Thread.sleep(500);
    }

    public static class TestApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            robot = new Robot();
            root = new StackPane();
            scene = new Scene(root);

            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN,
                    event -> Platform.runLater(() -> startupLatch.countDown()));
            primaryStage.show();
        }
    }

    private static class CustomTableViewSkin extends TableViewSkin {

        public CustomTableViewSkin(TableView control) {
            super(control);
        }

        @Override
        protected TableHeaderRow createTableHeaderRow() {
            return new CustomTableHeaderRow(this);
        }
    }

    private static class CustomTableHeaderRow extends TableHeaderRow {

        public CustomTableHeaderRow(TableViewSkinBase skin) {
            super(skin);
        }

        @Override
        protected void showColumnPopupMenu(Node anchor) {
            showColumnPopupMenuCalled = true;
            super.showColumnPopupMenu(anchor);
        }

    }

}
