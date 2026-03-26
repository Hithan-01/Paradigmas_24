package com.example.hospital_gateway.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Aspecto de Logging y Auditoría para el Hospital Gateway
 *
 * Este aspecto intercepta TODAS las llamadas a los endpoints del GatewayController
 * y registra información importante para auditoría y debugging.
 *
 * ¿Qué hace?
 * - Registra cada petición que llega al gateway
 * - Muestra los parámetros de entrada
 * - Mide el tiempo de ejecución
 * - Registra la respuesta
 * - Captura errores si ocurren
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Pointcut: Define DÓNDE se va a aplicar el aspecto
     * En este caso, intercepta TODOS los métodos públicos del GatewayController
     */
    @Pointcut("execution(* com.example.hospital_gateway.controller.GatewayController.*(..))")
    public void gatewayControllerMethods() {
        // Este método está vacío - solo define el punto de corte
    }

    /**
     * BEFORE: Se ejecuta ANTES de cada método del controller
     * Registra qué endpoint se está llamando y con qué parámetros
     */
    @Before("gatewayControllerMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String timestamp = LocalDateTime.now().format(formatter);
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        Object[] args = joinPoint.getArgs();

        logger.info("╔════════════════════════════════════════════════════════════════");
        logger.info("║ [AUDIT] Nueva petición recibida");
        logger.info("║ Timestamp: {}", timestamp);
        logger.info("║ Endpoint: {}.{}", className, methodName);
        logger.info("║ Parámetros: {}", Arrays.toString(args));
        logger.info("║ Ejecutando...");
    }

    /**
     * AFTER RETURNING: Se ejecuta DESPUÉS de que el método termine exitosamente
     * Registra la respuesta del endpoint
     */
    @AfterReturning(pointcut = "gatewayControllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();

        logger.info("║ [AUDIT] Petición completada exitosamente");
        logger.info("║ Método: {}", methodName);
        logger.info("║ Respuesta: {}", result != null ? result.getClass().getSimpleName() : "null");
        logger.info("║ Estado: ✓ EXITOSO");
    }

    /**
     * AFTER THROWING: Se ejecuta si el método lanza una excepción
     * Registra el error para debugging
     */
    @AfterThrowing(pointcut = "gatewayControllerMethods()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        String methodName = joinPoint.getSignature().getName();

        logger.error("║ [AUDIT] ✗ ERROR en la petición");
        logger.error("║ Método: {}", methodName);
        logger.error("║ Error: {}", error.getMessage());
        logger.error("║ Estado: ✗ FALLIDO");
    }

    /**
     * AROUND: Se ejecuta ALREDEDOR del método (antes y después)
     * Permite medir el tiempo de ejecución
     *
     * Este es el advice más poderoso porque puede:
     * - Ejecutar código antes del método
     * - Decidir si ejecutar o no el método
     * - Modificar los parámetros
     * - Modificar la respuesta
     * - Ejecutar código después del método
     */
    @Around("gatewayControllerMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        // Registrar inicio
        long startTime = System.currentTimeMillis();

        // Ejecutar el método original
        Object result = joinPoint.proceed();

        // Calcular tiempo de ejecución
        long executionTime = System.currentTimeMillis() - startTime;

        // Registrar tiempo
        logger.info("║ Tiempo de ejecución: {} ms", executionTime);

        // Si tardó más de 1 segundo, marcar como lento
        if (executionTime > 1000) {
            logger.warn("║ ⚠ ADVERTENCIA: Operación lenta detectada (> 1 segundo)");
        }

        logger.info("╚════════════════════════════════════════════════════════════════");

        return result;
    }

    /**
     * Pointcut adicional: Intercepta llamadas a servicios externos
     * Esto captura cuando el gateway llama a los otros microservicios
     */
    @Pointcut("execution(* com.example.hospital_gateway.service.HospitalGatewayService.*(..))")
    public void gatewayServiceMethods() {
    }

    /**
     * Registra cuando se hace una llamada a un servicio externo
     */
    @Before("gatewayServiceMethods()")
    public void logServiceCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("    ➜ Llamando a servicio externo: {}", methodName);
    }

    /**
     * Registra cuando termina la llamada a un servicio externo
     */
    @AfterReturning(pointcut = "gatewayServiceMethods()", returning = "result")
    public void logServiceReturn(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        logger.info("    ✓ Servicio {} respondió exitosamente", methodName);
    }
}
