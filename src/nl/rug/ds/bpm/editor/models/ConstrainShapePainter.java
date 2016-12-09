package nl.rug.ds.bpm.editor.models;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.shape.mxBasicShape;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import nl.rug.ds.bpm.editor.core.enums.ConstrainShape;
import nl.rug.ds.bpm.editor.core.enums.ConstraintStatus;
import nl.rug.ds.bpm.editor.models.graphModels.ConstrainEdgeCell;
import nl.rug.ds.bpm.editor.models.graphModels.InputLabelCell;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;

/**
 * Created by Mark on 13-7-2015.
 */
public class ConstrainShapePainter extends mxBasicShape {
    int left, startX, startY, top, size, sizeHalf;
    mxGraphics2DCanvas canvas;
    static int SAHPE_SPACING = 3;

    public void paintShape(mxGraphics2DCanvas canvas, mxCellState state) {
        super.paintShape(canvas, state);
        mxCell vertex = (mxCell) state.getCell();
        this.canvas = canvas;

        if (state.getCell() instanceof InputLabelCell) {
            /*InputLabelCell cell = (InputLabelCell) state.getCell();
            mxPoint point = state.getAbsoluteOffset();
            mxRectangle bounds = state.getLabelBounds();
            bounds.setX(bounds.getX() + 18);
            state.getBoundingBox().setWidth(state.getBoundingBox().getWidth() + 50);

            List<ConstrainShape> shapes = new ArrayList<>();
            shapes.add(ConstrainShape.ARROWEAST);
            shapes.add(ConstrainShape.ARROWEASTTFILLED);
            paintShape(shapes, state.getRectangle(), vertex.getGeometry(), point);*/
        } else if (vertex.getParent() instanceof ConstrainEdgeCell) {
            ConstrainEdgeCell cell = (ConstrainEdgeCell) vertex.getParent();
            List<ConstrainShape> shapes = cell.centerShapes();
            ConstraintStatus status = cell.getStatus();
            paintShape(shapes, state.getRectangle(), vertex.getGeometry(), new mxPoint(), cell.getStatus().getColor());
        }


    }

    Color color;

    private void paintShape(List<ConstrainShape> shapes, Rectangle rect, mxGeometry geo, mxPoint offset, Color color) {
        this.color = color;
        canvas.getGraphics().setColor(color);
        size = rect.height - 2;
        // size = 8;
        startX = rect.x + 2;
        startY = rect.y + (int) offset.getY();
        top = (int) Math.round(startY - size / 2);
        top -= 2;
        sizeHalf = size / 2;
        left = -8;

        int vertexWidth = (int) geo.getWidth();

        startX += (vertexWidth - (size + SAHPE_SPACING * shapes.size())) / 2;


        canvas.getGraphics().setColor(Color.black);
        for (ConstrainShape shape : shapes) {
            if (shape != null)
                switch (shape) {
                    case ARROWWEST:
                        addArrowLeft(false);
                        break;
                    case ARROWWESTFILLED:
                        addArrowLeft(true);
                        break;
                    case ARROWEAST:
                        addArrowRight(false);
                        break;
                    case ARROWEASTTFILLED:
                        addArrowRight(true);
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
        }

    }

    public void addRectangle(Boolean filled) {
        Polygon poly = new RotatePolygon(startX, top, size, RotatePolygon.Direction.LR);
        poly.addPoint(left, 0);
        poly.addPoint(left + size, 0);
        poly.addPoint(left + size, size);
        poly.addPoint(left, size);
        if (filled)
            canvas.fillShape(poly);
        canvas.getGraphics().setColor(Color.black);
        canvas.getGraphics().draw(poly);
        left += size + SAHPE_SPACING;

    }

    public void addArrowRight(Boolean filled) {
        Polygon poly = new RotatePolygon(startX, top, size, RotatePolygon.Direction.LR);
        poly.addPoint(left, 0);
        poly.addPoint(left + size, sizeHalf);
        poly.addPoint(left, size);
        if (filled)
            canvas.fillShape(poly);
        canvas.getGraphics().draw(poly);
        left += size + SAHPE_SPACING;
    }

    public void addArrowLeft(Boolean filled) {
        Polygon poly = new RotatePolygon(startX, top, size, RotatePolygon.Direction.LR);
        poly.addPoint(left + size, 0);
        poly.addPoint(left, sizeHalf);
        poly.addPoint(left + size, size);
        if (filled)
            canvas.fillShape(poly);
        canvas.getGraphics().draw(poly);
        left += size + SAHPE_SPACING;
    }

    public void addEllipse(Boolean filled) {
        Shape shape = new Ellipse2D.Double(startX + left, top, size, size);

        left += size + SAHPE_SPACING;
        if (filled)
            canvas.fillShape(shape);
        canvas.getGraphics().draw(shape);
    }

    public void addParallel(Boolean filled) {
        Polygon poly = new RotatePolygon(startX, top, size, RotatePolygon.Direction.LR);
        poly.addPoint(left + sizeHalf, -1);

        poly.addPoint(left + size + 1, sizeHalf);
        poly.addPoint(left + sizeHalf, size + 1);
        poly.addPoint(left - 1, sizeHalf);
        poly.addPoint(left + sizeHalf, -1);

        canvas.getGraphics().draw(poly);

        poly = new RotatePolygon(startX, top, size, RotatePolygon.Direction.LR);
        poly.addPoint(left + 2, sizeHalf);
        poly.addPoint(left + size - 2, sizeHalf);

        canvas.getGraphics().draw(poly);

        poly = new RotatePolygon(startX, top, size, RotatePolygon.Direction.LR);
        poly.addPoint(left + sizeHalf, 2);
        poly.addPoint(left + sizeHalf, size - 2);

        canvas.getGraphics().draw(poly);
        left += size + 3;
    }


    public void addExclusive(Boolean filled) {
        Polygon poly = new RotatePolygon(startX, top, size, RotatePolygon.Direction.LR);
        poly.addPoint(left + sizeHalf, -1);

        poly.addPoint(left + size + 1, sizeHalf);
        poly.addPoint(left + sizeHalf, size + 1);
        poly.addPoint(left - 1, sizeHalf);
        poly.addPoint(left + sizeHalf, -1);

        canvas.getGraphics().draw(poly);

        poly = new RotatePolygon(startX, top, size, RotatePolygon.Direction.LR);
        poly.addPoint(left + (int) Math.round(size * 0.34), (int) Math.round(size * 0.34));
        poly.addPoint(left + (int) Math.round(size * 0.66), (int) Math.round(size * 0.66));

        canvas.getGraphics().draw(poly);

        poly = new RotatePolygon(startX, top, size, RotatePolygon.Direction.LR);
        poly.addPoint(left + (int) Math.round(size * 0.66), (int) Math.round(size * 0.34));
        poly.addPoint(left + (int) Math.round(size * 0.34), (int) Math.round(size * 0.66));


        canvas.getGraphics().draw(poly);
        left += size + 3;

    }


}
