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
package com.sun.javafx.scene.control;

import javafx.scene.control.IndexedCell;
import javafx.scene.control.skin.VirtualFlow;

/**
 * Implementation of the {@link VirtualFlowModel} for a fixed cell size.
 * The fixed cell size allows us to easily calculate the necessary information.
 * This model is therefore not estimation-based and as such ignores all estimation requests.
 *
 * @param <T>
 *         the conrecte type of the {@link IndexedCell}
 */
public class FixedVirtualFlowModel<T extends IndexedCell> extends VirtualFlowModel<T> {

    /**
     * Creates the FixedVirtualFlowModel for the {@link VirtualFlow}.
     *
     * @param flow
     *         the {@link VirtualFlow}
     */
    public FixedVirtualFlowModel(VirtualFlow<T> flow) {
        super(flow);

        cellCountChanged();
    }

    @Override
    public void cellCountChanged() {
        setEstimatedSize(flow.getCellCount() * getFixedCellSize());
    }

    @Override
    public double computeBaseOffset(int toIndex) {
        if (flow.getCellCount() <= 0) {
            return 0;
        }

        return toIndex * getFixedCellSize();
    }

    @Override
    public int computeCurrentCellIndex(int cellCount) {
        if (cellCount <= 0) {
            return 0;
        }
        double absoluteOffset = flow.getAbsoluteOffset();

        int index = (int) Math.ceil(absoluteOffset / flow.getFixedCellSize());
        if (index >= cellCount) {
            return cellCount - 1;
        }
        return index;
    }

    @Override
    public double getCellHeight(T cell, double breadth) {
        return getFixedCellSize();
    }

    @Override
    public double getCellLength(int index) {
        return getFixedCellSize();
    }

    @Override
    public double getCellLength(T cell) {
        return getFixedCellSize();
    }

    @Override
    public double getCellWidth(T cell, double breadth) {
        return getFixedCellSize();
    }

    @Override
    public double getLineSize() {
        return getFixedCellSize();
    }

    @Override
    public void recalculateEstimatedSize(int fromIndex, int improvement) {
        // Nothing to do for us.
    }

    @Override
    public void updateCellSize(T cell) {
        // Nothing to do for us.
    }

    @Override
    protected double computeViewportOffsetImpl(int cellCount) {
        double absoluteOffset = flow.getAbsoluteOffset();
        if (absoluteOffset >= getEstimatedSize()) {
            return 0;
        }

        int currentIndex = (int) Math.floor(absoluteOffset / getFixedCellSize());
        double total = computeBaseOffset(currentIndex);
        return absoluteOffset - total;
    }

    private double getFixedCellSize() {
        return flow.getFixedCellSize();
    }
}
