/**
 * 
 */
package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

/**
 *
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThread;

    public MultiThreadedSumMatrix(final int nThread) {
        this.nThread = nThread;
    }

    private static class Worker extends Thread {

        private final double[][] matrix;

        private final MatrixRange range;

        private double result;

        Worker(final double[][] matrix, final MatrixRange range) {
            this.matrix = matrix;
            this.range = range;
        }

        @Override
        public void run() {
            DoubleStream mainStream = DoubleStream.empty();
            for (int i = this.range.getYStartIndex(); i < this.range.getYStartIndex() + this.range.getYSize(); i++) {
                mainStream = DoubleStream.concat(mainStream,
                        DoubleStream.of(this.matrix[i]).skip(this.range.getXStartIndex()).limit(this.range.getXSize()));
            }
            this.result = mainStream.sum();
        }

        public double getResult() {
            return this.result;
        }

    }

    private static class MatrixRange {

        private final int xStartIndex;
        private final int yStartIndex;
        private final int xSize;
        private final int ySize;

        MatrixRange(final int xStartIndex, final int yStartIndex, final int xSize, final int ySize) {
            this.xStartIndex = xStartIndex;
            this.yStartIndex = yStartIndex;
            this.xSize = xSize;
            this.ySize = ySize;
        }

        /**
         * Get the start index for the columns.
         * 
         * @return the start index for the columns
         */
        public int getXStartIndex() {
            return this.xStartIndex;
        }

        /**
         * Get the start index for the rows.
         * 
         * @return the start index for the rows
         */
        public int getYStartIndex() {
            return this.yStartIndex;
        }

        /**
         * Get the number of element to consider in the columns.
         * 
         * @return the number of element to consider in the columns
         */
        public int getXSize() {
            return this.xSize;
        }

        /**
         * Get the number of element to consider in the rows.
         * 
         * @return the number of element to consider in the rows
         */
        public int getYSize() {
            return this.ySize;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sum(final double[][] matrix) {
        final int matrixRowSize = matrix.length;
        final int matrixColumnSize = matrix[0].length;
        final int columnSize = matrixColumnSize % this.nThread + matrixColumnSize / this.nThread;
        final int rowSize = matrixRowSize % this.nThread + matrixRowSize / this.nThread;

        final List<Worker> workers = new ArrayList<>();

        for (int i = 0; i < matrixColumnSize; i += columnSize) {
            for (int j = 0; j < matrixRowSize; j += rowSize) {
                final int columnsSizeToUse = (i + columnSize) > matrixColumnSize ? (matrixColumnSize - i) : columnSize;
                final int rowsSizeToUse = (j + rowSize) > matrixRowSize ? (matrixRowSize - j) : rowSize;
                final MatrixRange mRange = new MatrixRange(i, j, columnsSizeToUse, rowsSizeToUse);
                workers.add(new Worker(matrix, mRange));
            }
        }

        for (final Worker w : workers) {
            w.start();
        }

        double sum = 0;
        for (final Worker w : workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }

}
