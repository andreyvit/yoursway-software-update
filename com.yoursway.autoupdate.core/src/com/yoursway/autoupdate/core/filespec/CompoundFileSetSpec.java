package com.yoursway.autoupdate.core.filespec;

import static com.google.common.base.Functions.TO_STRING;
import static com.google.common.base.Join.join;
import static com.google.common.collect.Iterables.transform;

import java.util.List;

import com.google.common.collect.Lists;
import com.yoursway.autoupdate.core.path.Path;

public class CompoundFileSetSpec implements FileSetSpec {
	
	private List<FileSetSpec> children = Lists.newArrayList();
	
	public void add(FileSetSpec spec) {
		children.add(spec);
	}

	public boolean contains(Path file) {
		for (FileSetSpec spec : children)
			if (spec.contains(file))
				return true;
		return false;
	}
	
	@Override
	public String toString() {
		if (children.size() == 1)
			return children.iterator().next().toString();
		else
			return "(" + join(" UNION ", transform(children, TO_STRING)) + ")";
	}

	public boolean isKnownToBeEmpty() {
		return children.isEmpty();
	}

}
