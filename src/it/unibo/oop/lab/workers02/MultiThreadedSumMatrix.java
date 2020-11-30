/**
 * 
 */
package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 *
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThread;

    public MultiThreadedSumMatrix(final int nThread) {
        this.nThread = nThread;
    }

    static class Worker extends Thread {

        private final double[][] matrix;

        private final MatrixRange range;

        private double result;

        Worker(final double[][] matrix, final MatrixRange range) {
            this.matrix = matrix;
            this.range = range;
        }

        @Override
        public void run() {
            /*
             * Check if i've gone too far with indices
             */
            if (this.range.getXStartIndex() >= this.matrix[0].length || this.range.getYStartIndex() >= this.matrix.length) {
                return;
            }
            /*
             * Clamp the size to make it not greater than what is possible
             */
            final int xSize = this.range.getXEndIndex() >= this.matrix[0].length
                    ? this.matrix[0].length - this.range.getXStartIndex()
                    : this.range.getXSize();
            final int ySize = this.range.getYEndIndex() >= this.matrix.length ? this.matrix.length - this.range.getYStartIndex()
                    : this.range.getYSize();

            this.result = Stream.of(this.matrix).skip(this.range.getYStartIndex()).limit(ySize)
                    .flatMap(row -> Stream.of(row).skip(this.range.getXStartIndex()).limit(xSize))
                    .mapToDouble(row -> DoubleStream.of(row).sum()).sum();
        }

        public double getResult() {
            return this.result;
        }

        @Override
        public String toString() {
            return "Worker [range=" + this.range + "]";
        }

    }

    static class MatrixRange {

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

        /**
         * Get the X end index.
         * 
         * @return the Y end index
         */
        public int getXEndIndex() {
            return this.xStartIndex + this.xSize - 1;
        }

        /**
         * Get the Y end index.
         * 
         * @return the Y end index
         */
        public int getYEndIndex() {
            return this.yStartIndex + this.ySize - 1;
        }

        @Override
        public String toString() {
            return "MatrixRange [xStartIndex=" + this.xStartIndex + ", yStartIndex=" + this.yStartIndex + ", xSize=" + this.xSize
                    + ", ySize=" + this.ySize + "]";
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sum(final double[][] matrix) {
        final int matrixRowSize = matrix.length;
        final int matrixColumnSize = matrix[0].length;
        /**
         * I used the formula that has been used in workers01
         */
        final int columnSize = matrixColumnSize % this.nThread + matrixColumnSize / this.nThread;
        final int rowSize = matrixRowSize % this.nThread + matrixRowSize / this.nThread;

        final List<Worker> workers = new ArrayList<>();

        for (int i = 0; i < matrixColumnSize; i += columnSize) {
            for (int j = 0; j < matrixRowSize; j += rowSize) {
                final MatrixRange mRange = new MatrixRange(i, j, columnSize, rowSize);
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
