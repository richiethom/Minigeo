package eu.jacquet80.minigeo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import lombok.Builder;
import lombok.Data;

/**
 * Segment of a linear shape, such as a road.
 * 
 * @author Christophe Jacquet
 *
 */
@Builder
@Data
public class Segment {
	private final static Stroke BASIC_STROKE = new BasicStroke();
	
	private final Point pointA;
	private final Point pointB;
	private final Color color;
	@Builder.Default
	private final Stroke stroke = BASIC_STROKE;
	
}
