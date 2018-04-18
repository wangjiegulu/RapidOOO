package com.wangjiegulu.rapidooo.depmodule.bll.xbo.ext;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.ext.Abbey;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.ext.Mabel;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.ext.Person;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 18/04/2018.
 */
@OOOs(suffix = "BO",
        ooos = {
                @OOO(id = "id_bo_person", from = Person.class),
                @OOO(from = Abbey.class, targetSupperTypeId = "id_bo_person"),
                @OOO(from = Mabel.class, targetSupperTypeId = "id_bo_person")
        }
)
public class ExtBOGenerator {
}
