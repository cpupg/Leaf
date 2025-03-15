package com.sankuai.inf.leaf.boot.segment;

import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;

import java.util.Collections;
import java.util.List;

/**
 * 实现类。
 *
 * @author chen
 */
public class IdAllocDaoImpl implements IDAllocDao {
    @Override
    public List<LeafAlloc> getAllLeafAllocs() {
        return Collections.emptyList();
    }

    @Override
    public LeafAlloc updateMaxIdAndGetLeafAlloc(String tag) {
        return null;
    }

    @Override
    public LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc) {
        return null;
    }

    @Override
    public List<String> getAllTags() {
        return Collections.emptyList();
    }

    @Override
    public LeafAlloc resetLeafAlloc(String key, int step) {
        return null;
    }
}
