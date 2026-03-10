import { saveAs } from 'file-saver';
import { errorCode } from '@/types/ErrorCode';
import { ElLoading } from 'element-plus';

export const downloadUtils = {
	/**
	 * 下载zip文件
	 * @param url 下载地址
	 * @param fileName 文件名
	 * @param params 参数
	 */
	zip(url: string, fileName: string, params?: any) {
		const loading = ElLoading.service({ text: "正在下载数据，请稍候", spinner: "el-icon-loading", background: "rgba(0, 0, 0, 0.7)", })
		service({
			method: "get",
			url: url,
			responseType: 'blob', // 一定要添加该设置，异步请求默认的返回格式是responseType: 'text'，如果是默认格式Axios会尝试将后台传回的二进制转为字符串，会导致文件损坏，responseType: 'blob'表示告诉Axios返回的是二进制数据，不要尝试转换
			params: params || {}, // 无参数时传空对象
		}).then((response) => {
			const isBlob = response.data.type !== 'application/json'
			if (isBlob) {
				// 创建 Blob 对象并保存文件
				const blob = new Blob([response.data], { type: 'application/zip' })
				this.saveAs(blob, fileName)
			} else {
				// 处理非二进制响应（通常是错误信息）
				this.printErrMsg(response.data)
			}
			loading.close()
		}).catch((r: any) => {
      console.error(r)
      ElMessage.error('下载文件出现错误，请联系管理员！')
      loading.close()
    });
  },
	/**
	 * 下载文件
	 * @param text 文件内容
	 * @param fileName 文件名
	 * @param opts 配置项
	 */
	saveAs(text: any, fileName: string, opts?: any) {
    saveAs(text, fileName, opts)
  },
	/**
	 * 打印错误信息
	 * @param data 响应数据
	 */
  async printErrMsg(data: any) {
    const resText = await data.text()
    const rspObj = JSON.parse(resText)
    const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default']
    ElMessage.error(errMsg)
  }
}
