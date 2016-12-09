package nl.rug.ds.bpm.editor.models;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxIMarker;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import nl.rug.ds.bpm.editor.core.enums.ConstrainShape;
import nl.rug.ds.bpm.editor.models.graphModels.EdgeCell;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;

/**
 * Created by Mark Kloosterhuis.
 */
public class ConstrainMarker implements mxIMarker {
    RotatePolygon.Direction direction;
    int left, startX, startY, top, size, sizeHalf;
    double nx, ny;
    mxGraphics2DCanvas canvas;

    @Override
    public mxPoint paintMarker(mxGraphics2DCanvas canvas, mxCellState state, String type, mxPoint pe, double nx, double ny, double dsize, boolean source) {
        if (state.getCell() instanceof EdgeCell) {
            EdgeCell cell = (EdgeCell) state.getCell();

            //String direction = mxUtils.getString(state.getStyle(), mxConstants.STYLE_DIRECTION, mxConstants.DIRECTION_EAST);
            //direction.equals(mxConstants.DIRECTION_NORTH)
            this.canvas = canvas;
            this.nx = nx;
            this.ny = ny;
            left = 3;
            direction = RotatePolygon.Direction.LR;
            if (nx > 0)
                direction = RotatePolygon.Direction.RL;
            else if (ny < 0)
                direction = RotatePolygon.Direction.TB;
            else if (ny > 0)
                direction = RotatePolygon.Direction.BT;

            startX = (int) Math.round(pe.getX());
            startY = (int) Math.round(pe.getY());
            top = (int) Math.round(startY - size / 2)-1;
            size = (int) Math.round(dsize);
            sizeHalf = size / 2;
            List<ConstrainShape> shapes = source ? cell.startShapes() : cell.endShapes();
            for (ConstrainShape shape : shapes) {
                Polygon poly = null;
                if (shape != null) {
                    switch (shape) {
                        case ARROWWEST:
                            poly = source ? addArrowLeft(false) : addArrowRight(false);
                            break;
                        case ARROWWESTFILLED:
                            poly = source ? addArrowLeft(true) : addArrowRight(true);
                            break;
                        case ARROWEAST:
                            poly = source ? addArrowRight(false) : addArrowLeft(false);
                            break;
                        case ARROWEASTTFILLED:
                            poly = source ? addArrowRight(true) : addArrowLeft(true);
                            break;
                        case ECLIPSE:
                            addEllipse(false);
                            break;
                        case ECLIPSEFILLED:
                            addEllipse(true);
                            break;
                        case RECTANGLE:
                            addRectangle(false);
                            break;
                        case RECTANGLEFILLED:
                            addRectangle(true);
                            break;
                        case EXCLUSIVE:
                            addExclusive(true);
                            break;
                        case PARALLEL:
                            addParallel(true);
                            break;

                    }
                } else {
                    shape = shape;
                }
            }
            left -= size;
            mxPoint returnPoint = new mxPoint(-nx + (left), -ny);

            if (direction == RotatePolygon.Direction.RL)
                returnPoint = new mxPoint(-nx - (left), -ny);
            else if (direction == RotatePolygon.Direction.TB)
                returnPoint = new mxPoint(-nx, -ny + (left));
            else if (direction == RotatePolygon.Direction.BT)
                returnPoint = new mxPoint(-nx, -ny - (left));


            return returnPoint;
        }
        return null;
    }
    public void addParallel(Boolean filled) {
        Polygon poly = new RotatePolygon(startX, top, size, direction);
        poly.addPoint(left+sizeHalf, -1);

        poly.addPoint(left + size+1, sizeHalf);
        poly.addPoint(left+sizeHalf, size+1);
        poly.addPoint(left-1, sizeHalf);
        poly.addPoint(left+sizeHalf, -1);

        canvas.getGraphics().draw(poly);

        poly = new RotatePolygon(startX, top, size, direction);
        poly.addPoint(left+2, sizeHalf);
        poly.addPoint(left+size-2, sizeHalf);

        canvas.getGraphics().draw(poly);

        poly = new RotatePolygon(startX, top, size, direction);
        poly.addPoint(left+sizeHalf, 2);
        poly.addPoint(left+sizeHalf, size-2);

        canvas.getGraphics().draw(poly);
        left += size + 3;
    }


    public void addExclusive(Boolean filled) {
        Polygon poly = new RotatePolygon(startX, top, size, direction);
        poly.addPoint(left+sizeHalf, -1);

        poly.addPoint(left + size+1, sizeHalf);
        poly.addPoint(left+sizeHalf, size+1);
        poly.addPoint(left-1, sizeHalf);
        poly.addPoint(left+sizeHalf, -1);

        canvas.getGraphics().draw(poly);

        poly = new RotatePolygon(startX, top, size, direction);
        poly.addPoint(left+(int) Math.round(size*0.34), (int) Math.round(size*0.34));
        poly.addPoint(left+(int) Math.round(size*0.66), (int) Math.round(size*0.66));

        canvas.getGraphics().draw(poly);

        poly = new RotatePolygon(startX, top, size, direction);
        poly.addPoint(left+(int) Math.round(size*0.66), (int) Math.round(size*0.34));
        poly.addPoint(left+(int) Math.round(size*0.34), (int) Math.round(size*0.66));


        canvas.getGraphics().draw(poly);
        left += size + 3;

    }



    public void addEllipse(Boolean filled) {
        Shape shape = null;
        if (direction == RotatePolygon.Direction.LR)
            shape = new Ellipse2D.Double(startX + left, top, size, size);
        if (direction == RotatePolygon.Direction.RL)
            shape = new Ellipse2D.Double(startX - size - left, top, size, size);
        else if (direction == RotatePolygon.Direction.TB) {

            shape = new Ellipse2D.Double(startX - (size / 2), top, size, size);
        } else if (direction == RotatePolygon.Direction.BT) {
            shape = new Ellipse2D.Double(startX - size / 2, top - left, size, size);
        }
        left += size + 3;
        if (filled)
            canvas.fillShape(shape);
        canvas.getGraphics().draw(shape);

    }

    public void addRectangle(Boolean filled) {
        Polygon poly = new RotatePolygon(startX, top, size, direction);
        poly.addPoint(left, 0);
        poly.addPoint(left + size, 0);
        poly.addPoint(left + size, size);
        poly.addPoint(left, size);
        if (filled)
            canvas.fillShape(poly);
        canvas.getGraphics().draw(poly);
        left += size + 3;
    }

    public Polygon addArrowRight(Boolean filled) {
        Polygon poly = new RotatePolygon(startX, top, size, direction);
        poly.addPoint(left, 0);
        poly.addPoint(left + size, sizeHalf);
        poly.addPoint(left, size);
        if (filled)
            canvas.fillShape(poly);
        canvas.getGraphics().draw(poly);
        left += size + 3;
        return poly;
    }

    public Polygon addArrowLeft(Boolean filled) {
        Polygon poly = new RotatePolygon(startX, top, size, direction);
        poly.addPoint(left + size, 0);
        poly.addPoint(left, sizeHalf);
        poly.addPoint(left + size, size);
        if (filled)
            canvas.fillShape(poly);
        canvas.getGraphics().draw(poly);
        left += size + 3;
        return poly;
    }

    public mxPoint returnPoint() {
        mxPoint returnPoint = new mxPoint(-nx + (left), -ny);
        if (direction == RotatePolygon.Direction.RL)
            returnPoint = new mxPoint(-nx - (left), -ny);
        else if (direction == RotatePolygon.Direction.TB)
            returnPoint = new mxPoint(-nx, -ny + (left));
        else if (direction == RotatePolygon.Direction.BT)
            returnPoint = new mxPoint(-nx, -ny - (left));


        return returnPoint;
    }
}