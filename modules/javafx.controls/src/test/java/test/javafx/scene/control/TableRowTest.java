/*
 * Copyright (c) 2026, Oracle and/or its affiliates. All rights reserved.
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

import javafx.collections.FXCollections;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils;
import test.com.sun.javafx.scene.control.infrastructure.StageLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TableRowTest {

    private TableRow<String> cell;
    private TableView<String> table;

    private static final String APPLES = "Apples";
    private static final String ORANGES = "Oranges";
    private static final String PEARS = "Pears";

    StageLoader stageLoader;

    @BeforeEach
    public void setup() {
        cell = new TableRow<>();

        table = new TableView<>(FXCollections.observableArrayList(List.of(APPLES, ORANGES, PEARS)));
    }

    @AfterEach
    public void after() {
        if (stageLoader != null) {
            stageLoader.dispose();
        }
    }

    @Test
    public void focusOnFocusModelIsReflectedInCells() {
        cell.updateTableView(table);
        cell.updateIndex(0);

        TableRow<String> other = new TableRow<>();
        other.updateTableView(table);
        other.updateIndex(1);

        table.getFocusModel().focus(0);
        assertTrue(isFakeFocused(cell));
        assertFalse(isFakeFocused(other));
    }

    @Test public void changesToFocusOnFocusModelAreReflectedInCells() {
        cell.updateTableView(table);
        cell.updateIndex(0);

        TableRow<String> other = new TableRow<>();
        other.updateTableView(table);
        other.updateIndex(1);

        table.getFocusModel().focus(0);
        table.getFocusModel().focus(1);
        assertFalse(isFakeFocused(cell));
        assertTrue(isFakeFocused(other));
    }

    private static boolean isFakeFocused(TableRow<String> cell) {
        return ControlTestUtils.isFakeFocused(cell);
    }
}
