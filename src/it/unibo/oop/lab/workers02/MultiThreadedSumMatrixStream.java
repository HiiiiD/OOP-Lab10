/**
 *
 */
package it.unibo.oop.lab.workers02;

import java.util.stream.IntStream;

import it.unibo.oop.lab.workers02.MultiThreadedSumMatrix.MatrixRange;
import it.unibo.oop.lab.workers02.MultiThreadedSumMatrix.Worker;

/**
 *
 */
public class MultiThreadedSumMatrixStream implements SumMatrix {

    private final int nThreads;

    public MultiThreadedSumMatrixStream(final int nThreads) {
        this.nThreads = nThreads;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double sum(final double[][] matrix) {
        final int matrixRowSize = matrix.length;
        final int matrixColumnSize = matrix[0].length;
        final int columnSize = matrixColumnSize % this.nThreads + matrixColumnSize / this.nThreads;
        final int rowSize = matrixRowSize % this.nThreads + matrixRowSize / this.nThreads;

        return IntStream.iterate(0, start -> start + columnSize).limit(this.nThreads).boxed()
                .flatMap(colIndex -> IntStream.iterate(0, start -> start + rowSize).limit(this.nThreads).boxed()
                        .map(rowIndex -> new Worker(matrix, new MatrixRange(colIndex, rowIndex, columnSize, rowSize))))
                .peek(Worker::start).peek(MultiThreadedSumMatrixStream::joinUninterruptibly).mapToDouble(Worker::getResult).sum();
    }

    /**
     * Copied from
     * {@link it.unibo.oop.lab.workers01.MultiThreadedListSumWithStreams} to use
     * method reference without handling errors.
     *
     * @param target
     */
    private static void joinUninterruptibly(final Thread target) {
        var joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
