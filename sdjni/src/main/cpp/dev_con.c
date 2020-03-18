/*
 * Copyright (c) lnwdl (lnwdl@163.com)
 * All rights reserved.
 */
#include <string.h>
#include <stdlib.h>
#include "sdkey.h"
#include "skf.h"
#include "logger.h"

#define CON_NAME    "con_test"

int dev_con_test(sdkey_data_t *data)
{
    ULONG rv, listLen, inLen, outLen, conType;
    CHAR conList[256];
    HCONTAINER hCon;
    BYTE icert[2048], ocert[2048];
    int ret = 0, num;

    SKF_DeleteContainer(data->hApp, CON_NAME);
    rv = SKF_CreateContainer(data->hApp, CON_NAME, &hCon);
    if (rv != SAR_OK) {
        LOGE("SKF_CreateContainer ERROR, errno[0x%08x]\n", rv);
        goto error;
    }
    LOGI("create container[%s] ok\n", CON_NAME);

    rv = SKF_CloseContainer(hCon);
    if (rv != SAR_OK) {
        LOGE("SKF_CloseContainer ERROR, errno[0x%08x]\n", rv);
        goto error;
    }
    hCon = NULL;
    LOGI("close container[%s] ok\n", CON_NAME);

    listLen = sizeof (conList) / sizeof (CHAR);
    rv = SKF_EnumContainer(data->hApp, conList, &listLen);
    if (rv != SAR_OK) {
        LOGE("SKF_EnumContainer ERROR, errno[0x%08x]\n", rv);
        goto error;
    }

    if (!NameListFind(conList, listLen, CON_NAME)) {
        LOGE("Find container name[%s] ERROR\n", CON_NAME);
        goto error;
    }
    LOGI("find container[%s] ok\n", CON_NAME);

    rv = SKF_OpenContainer(data->hApp, CON_NAME, &hCon);
    if (rv != SAR_OK) {
        LOGE("SKF_OpenContainer ERROR, errno[0x%08x]\n", rv);
        goto error;
    }
    LOGI("open container[%s] ok\n", CON_NAME);

    rv = SKF_GetContainerType(hCon, &conType);
    if (rv != SAR_OK) {
        LOGE("SKF_GetContainerType ERROR, errno[0x%08x]\n", rv);
        goto error;
    }
    switch (conType) {
        case 0:
            LOGI("container[%s] type[UNKNOWN] ok\n", CON_NAME);
            break;
        case 1:
            LOGI("container[%s] type[RSA] error\n", CON_NAME);
            break;
        case 2:
            LOGI("container[%s] type[ECC] error\n", CON_NAME);
            break;
        default:
            LOGI("container[%s] type[%ld] error\n", CON_NAME, conType);
            break;
    }

    /* import/export sign cert */
    inLen = 1234;
    memset(icert, 0x02, inLen);
    rv = SKF_ImportCertificate(hCon, TRUE, icert, inLen);
    if (rv != SAR_OK) {
        LOGE("SKF_ImportCertificate ERROR, errno[0x%08x]\n", rv);
        goto error;
    }

    outLen = sizeof (ocert) / sizeof (BYTE);
    rv = SKF_ExportCertificate(hCon, TRUE, ocert, &outLen);
    if (rv != SAR_OK) {
        LOGE("SKF_ExportCertificate ERROR, errno[0x%08x]\n", rv);
        goto error;
    }

    if (inLen != outLen || memcmp(icert, ocert, inLen) != 0) {
        LOGE("container[%s] import/export certificate ERROR\n");
        goto error;
    }
    LOGI("container[%s] import/export sign cert ok\n", CON_NAME);

    ret = 1;
error:
    if (hCon) SKF_CloseContainer(hCon);

    return ret;
}
