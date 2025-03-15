package com.sankuai.inf.leaf.server.dao;

import com.sankuai.inf.leaf.segment.dao.IDAllocDao;
import com.sankuai.inf.leaf.segment.model.LeafAlloc;
import org.apache.ibatis.annotations.Param;

public interface IDAllocMapper extends IDAllocDao {
    int insertLeafAlloc(LeafAlloc leafAlloc);

    LeafAlloc getLeafAlloc(@Param("tag") String tag);
}
