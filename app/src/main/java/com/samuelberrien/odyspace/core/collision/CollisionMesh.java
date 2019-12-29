package com.samuelberrien.odyspace.core.collision;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Samuel on 09/10/2017.
 */

public class CollisionMesh {

	private Context context;

	private final int DIM_VERTEX = 3;
	private final int NB_VERTEX_PER_TRIANLGE = 3;
	private float[] vertices;

	public CollisionMesh(Context context, String objFileName) {
		this.context = context;
		try {
			InputStream inputStream = this.context.getAssets().open(objFileName);
			InputStreamReader inputreader = new InputStreamReader(inputStream);
			parseObjVertex(inputreader);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			vertices = new float[0];
		}
	}

	private void parseObjVertex(InputStreamReader inputStreamReader) {
		BufferedReader buffreader1 = new BufferedReader(inputStreamReader);
		String line;

		ArrayList<Float> vertixsList = new ArrayList<>();
		ArrayList<Integer> vertexDrawOrderList = new ArrayList<>();

		try {
			while ((line = buffreader1.readLine()) != null) {
				if (line.startsWith("v ")) {
					String[] tmp = line.split(" ");
					vertixsList.add(Float.parseFloat(tmp[1]));
					vertixsList.add(Float.parseFloat(tmp[2]));
					vertixsList.add(Float.parseFloat(tmp[3]));
				} else if (line.startsWith("f")) {
					String[] tmp = line.split(" ");
					vertexDrawOrderList.add(Integer.parseInt(tmp[1].split("/")[0]));
					vertexDrawOrderList.add(Integer.parseInt(tmp[2].split("/")[0]));
					vertexDrawOrderList.add(Integer.parseInt(tmp[3].split("/")[0]));
				}
			}

			buffreader1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		vertices = new float[3 * vertexDrawOrderList.size()];
		for (int i = 0; i < vertexDrawOrderList.size(); i++) {
			int offset = 3 * i;
			vertices[offset] = vertixsList.get((vertexDrawOrderList.get(i) - 1) * 3);
			vertices[offset + 1] = vertixsList.get((vertexDrawOrderList.get(i) - 1) * 3 + 1);
			vertices[offset + 2] = vertixsList.get((vertexDrawOrderList.get(i) - 1) * 3 + 2);
		}
	}

	public float[] cloneVertices() {
		return vertices.clone();
	}
}
