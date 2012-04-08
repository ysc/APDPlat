package com.apdplat.platform.service;

import com.apdplat.platform.common.Common;
import com.apdplat.platform.model.Model;
import java.util.List;

public interface Service<T extends Model>  extends Common<T> {
	public List<Exception> delete(Integer[] modelIds);
}