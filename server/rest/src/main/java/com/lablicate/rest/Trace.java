package com.lablicate.rest;

import java.util.List;

class Trace {

	// Have to be public or having public getter
	public List<Double> x;
	public List<Double> y;
	public String type;

	public Trace(List<Double> x, List<Double> y) {
		super();
		this.x = x;
		this.y = y;
		this.type = "scatter";
	}

}
