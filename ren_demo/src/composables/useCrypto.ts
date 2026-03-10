// src/composables/useCrypto.ts
import CryptoJS from 'crypto-js';

// 加密配置类型
interface CryptoConfig {
  key: string; // 加密密钥（必填，建议长度32字符）
  iv?: string; // 初始化向量（可选，建议长度16字符）
}

// 默认配置（从环境变量获取）
const DEFAULT_CONFIG: CryptoConfig = {
  key: import.meta.env.VITE_CRYPTO_KEY,
  iv: import.meta.env.VITE_CRYPTO_IV
};

export const useCrypto = (userConfig?: CryptoConfig) => {
  // 合并配置：用户配置优先，没有则使用默认配置
  const config = {
    ...DEFAULT_CONFIG,
    ...userConfig
  };

  // 检查密钥是否存在
  if (!config.key) {
    throw new Error('加密密钥未配置！请设置VITE_CRYPTO_KEY环境变量或传入key参数');
  }

  // 验证密钥长度
  if (config.key.length !== 32) {
    console.warn('建议使用32字符长度的密钥以获得最佳安全性');
  }

  // 默认使用CBC模式，如果提供iv则使用，否则使用ECB模式
  const useIV = config.iv && config.iv.length >= 16;

  /**
   * AES加密
   * @param plainText 明文
   * @returns Base64格式的密文
   */
  const encrypt = (plainText: string): string => {
    const key = CryptoJS.enc.Utf8.parse(config.key!);

    if (useIV) {
      const iv = CryptoJS.enc.Utf8.parse(config.iv!.substring(0, 16));
      return CryptoJS.AES.encrypt(plainText, key, {
        iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
      }).toString();
    }

    return CryptoJS.AES.encrypt(plainText, key, {
      mode: CryptoJS.mode.ECB,
      padding: CryptoJS.pad.Pkcs7
    }).toString();
  };

  /**
   * AES解密
   * @param cipherText Base64格式的密文
   * @returns 解密后的明文
   */
  const decrypt = (cipherText: string): string => {
    const key = CryptoJS.enc.Utf8.parse(config.key!);

    if (useIV) {
      const iv = CryptoJS.enc.Utf8.parse(config.iv!.substring(0, 16));
      const bytes = CryptoJS.AES.decrypt(cipherText, key, {
        iv,
        mode: CryptoJS.mode.CBC,
        padding: CryptoJS.pad.Pkcs7
      });
      return bytes.toString(CryptoJS.enc.Utf8);
    }

    const bytes = CryptoJS.AES.decrypt(cipherText, key, {
      mode: CryptoJS.mode.ECB,
      padding: CryptoJS.pad.Pkcs7
    });
    return bytes.toString(CryptoJS.enc.Utf8);
  };

  return {
    encrypt,
    decrypt
  };
}
