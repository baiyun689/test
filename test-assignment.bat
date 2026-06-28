@echo off
echo ========================================
echo  测试审查员分配系统
echo ========================================

echo.
echo [1] 查看所有错误类型及其审查员分配:
echo ---------------------------------------
curl -s http://localhost:8080/errors/types | python -m json.tool
echo.

echo [2] 提交 5 种不同的错误到队列:
echo ---------------------------------------
curl -s -X POST "http://localhost:8080/errors/submit?type=SQL_INJECTION&detail=拼接未过滤用户输入"
echo.
curl -s -X POST "http://localhost:8080/errors/submit?type=NULL_POINTER&detail=getEmailList未检查null"
echo.
curl -s -X POST "http://localhost:8080/errors/submit?type=RESOURCE_LEAK&detail=BufferedReader未关闭"
echo.
curl -s -X POST "http://localhost:8080/errors/submit?type=PATH_TRAVERSAL&detail=fileName未校验"
echo.
curl -s -X POST "http://localhost:8080/errors/submit?type=HARDCODED_SECRET&detail=密钥写死在代码中"
echo.

echo.
echo [3] 再提交 2 个同样的 SQL_INJECTION 错误:
echo     (验证: 这 3 个 SQL_INJECTION 都应该分给同一个人)
echo ---------------------------------------
curl -s -X POST "http://localhost:8080/errors/submit?type=SQL_INJECTION&detail=email字段拼接"
echo.
curl -s -X POST "http://localhost:8080/errors/submit?type=SQL_INJECTION&detail=domain参数拼接"
echo.

echo.
echo [4] 立即分发(不等定时器):
echo ---------------------------------------
curl -s -X POST http://localhost:8080/errors/dispatch-now | python -m json.tool
echo.

echo.
echo [5] 核心验证——人工检查上面的输出:
echo     ^> SQL_INJECTION    的 3 个错误 是否全部给了 R001(张三)?
echo     ^> NULL_POINTER     的 1 个错误 是否给了 R002(李四)?
echo     ^> RESOURCE_LEAK    的 1 个错误 是否给了 R003(王五)?
echo     ^> PATH_TRAVERSAL   的 1 个错误 是否给了 R004(赵六)?
echo     ^> HARDCODED_SECRET 的 1 个错误 是否给了 R005(孙七)?
echo.
echo 如果上面全部"是",则调度员分配正确。
echo 如果同一类型出现了不同的 reviewerId,则有 bug。
echo ========================================
pause
