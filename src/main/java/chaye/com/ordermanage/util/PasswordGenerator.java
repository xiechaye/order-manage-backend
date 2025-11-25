package chaye.com.ordermanage.util;

/**
 * 密码生成器工具
 * 用于验证和生成BCrypt密码哈希
 */
public class PasswordGenerator {

    // 这是"123456"的标准BCrypt哈希，可以手动更新数据库
    public static final String PASSWORD_123456_HASH = "$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW";
    
    // 数据库中的密码哈希格式（实际上不是你想要的123456）
    // 可以从日志取得: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE.sQCSGvG.kaqLK
}