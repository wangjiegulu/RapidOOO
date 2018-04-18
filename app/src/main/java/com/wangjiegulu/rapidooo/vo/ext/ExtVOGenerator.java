package com.wangjiegulu.rapidooo.vo.ext;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.ext.AbbeyBO;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.ext.MabelBO;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.ext.PersonBO;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 18/04/2018.
 */
@OOOs(suffix = "VO", fromSuffix = "BO",
        ooos = {
                @OOO(id = "id_vo_person", from = PersonBO.class),
                @OOO(from = AbbeyBO.class, targetSupperTypeId = "id_vo_person"),
                @OOO(from = MabelBO.class, targetSupperTypeId = "id_vo_person")
        }
)
public class ExtVOGenerator {
}
