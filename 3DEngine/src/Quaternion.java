public class Quaternion {
	/*
	public Vector3 rotate( Vector3 relPos , double anglex, double angley) {
	    Vector3 xTransform = new Vector3(relPos.x*Math.cos(anglex)- relPos.z * Math.sin(anglex) , relPos.y , relPos.z * Math.cos(anglex) + relPos.x *Math.sin(anglex));
	    //relPos =  new Vector3(xTransform.x*Math.cos(angley)- xTransform.y * Math.sin(angley) , xTransform.y * Math.cos(angley) + xTransform.x *Math.sin(angley),xTransform.z  );
	    return xTransform;

	}*/

	public Vector3 rotate(Vector3 camPos, Vector3 Pos, double anglex, double angley) {
		Vector3 xTransform = new Vector3(((Pos.x - camPos.x) * Math.cos(anglex)) - ((Pos.z - camPos.z) * Math.sin(anglex)), (Pos.y - camPos.y), ((Pos.z - camPos.z) * Math.cos(anglex)) + ((Pos.x - camPos.x) * Math.sin(anglex)));
		xTransform = new Vector3(xTransform.x * Math.cos(angley) - xTransform.y * Math.sin(angley), xTransform.y * Math.cos(angley) + xTransform.x * Math.sin(angley), xTransform.z);
		xTransform.x += camPos.x;
		xTransform.y += camPos.y;
		xTransform.z += camPos.z;
		return xTransform;
	}

	public double degToRadian(double degree) {
		return (degree * Math.PI / 180);
	}
}
