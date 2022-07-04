import java.awt.Color;

public class light {
	Vector3 pos;
	Color tint = new Color(255, 255, 255);
	double intensity = 0.5;
	public light(Vector3 a) {
		pos = a;
	}
}
