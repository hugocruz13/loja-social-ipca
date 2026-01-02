/**
 * index.js - Código Corrigido para Base de Dados Nomeada
 */

const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const { setGlobalOptions } = require("firebase-functions/v2");
const logger = require("firebase-functions/logger");
const nodemailer = require('nodemailer');

// --- NOVOS IMPORTS MAIS SEGUROS ---
const { initializeApp } = require("firebase-admin/app");
const { getFirestore } = require("firebase-admin/firestore"); // Import direto
const { getMessaging } = require("firebase-admin/messaging");

// Inicializa a App
initializeApp();

// Prepara a ligação à base de dados ESPECÍFICA (fora da função para ser mais rápido)
// Isto garante que 'db' aponta SEMPRE para a 'loja-social-ipca-db'
const db = getFirestore("loja-social-ipca-db");

// --- CONFIGURAÇÃO GLOBAL ---
setGlobalOptions({ region: "europe-west1" });

// --- CONFIGURAÇÃO DO GMAIL ---
const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'lojasocial.ipca@gmail.com',
        pass: 'kxjn dkqn zpxb edbo' // <--- A TUA PASSWORD DE APP AQUI
    }
});

// ==========================================
// 1. FUNÇÃO DE ENVIAR EMAILS
// ==========================================
exports.sendEmailManual = onDocumentCreated({
    document: "mail_queue/{docId}",
    database: "loja-social-ipca-db", // Obrigatório
    region: "europe-west1"
}, async (event) => {

    const snapshot = event.data;
    if (!snapshot) return;

    const data = snapshot.data();
    logger.info("Tentativa de envio de email para:", data.to);

    const mailOptions = {
        from: 'Loja Social <lojasocial.ipca@gmail.com>',
        to: data.to,
        subject: data.message.subject,
        html: data.message.html || data.message.text
    };

    if (data.message.replyTo) mailOptions.replyTo = data.message.replyTo;
    if (data.message.from) mailOptions.from = data.message.from;

    try {
        await transporter.sendMail(mailOptions);
        logger.info("Email enviado com sucesso!");
        
        return snapshot.ref.update({ 
            delivery: { state: 'SUCCESS', date: new Date().toISOString() } 
        });
    } catch (error) {
        logger.error("Erro ao enviar email:", error);
        return snapshot.ref.update({ 
            delivery: { state: 'ERROR', error: error.toString() } 
        });
    }
});

// ==========================================
// 2. FUNÇÃO DE ENVIAR NOTIFICAÇÕES PUSH
// ==========================================
exports.sendPushNotification = onDocumentCreated({
    document: "notifications_queue/{docId}",
    database: "loja-social-ipca-db", // Obrigatório
    region: "europe-west1"
}, async (event) => {
    
    const snapshot = event.data;
    if (!snapshot) return;

    const data = snapshot.data();
    logger.info("Iniciando envio de notificação para User ID:", data.userId);

    try {
        // AQUI ESTAVA O PROBLEMA ANTES. 
        // Agora usamos a constante 'db' que definimos no topo, apontada para a base certa.
        
        // 1. Procurar o utilizador (Cascata: Beneficiários -> Colaboradores)
        let userDoc = await db.collection('beneficiarios').doc(data.userId).get();

        if (!userDoc.exists) {
            logger.info(`Não encontrado em 'beneficiarios', a tentar 'colaboradores'...`);
            userDoc = await db.collection('colaboradores').doc(data.userId).get();
        }

        if (!userDoc.exists) {
            logger.warn("Utilizador não encontrado em nenhuma coleção:", data.userId);
            // Atualiza o estado para sabermos que falhou aqui
            return snapshot.ref.update({ status: 'USER_NOT_FOUND' });
        }

        // 2. Obter o Token
        const userData = userDoc.data();
        const fcmToken = userData.fcmToken;

        if (!fcmToken) {
            logger.warn("Utilizador encontrado, mas SEM token FCM:", data.userId);
            return snapshot.ref.update({ status: 'NO_TOKEN' });
        }

        // 3. Enviar a Notificação
        const messagePayload = {
            token: fcmToken,
            notification: {
                title: data.title,
                body: data.message
            },
            data: data.data || {} 
        };

        await getMessaging().send(messagePayload);
        
        logger.info("Notificação Push enviada com sucesso!");
        return snapshot.ref.update({ status: 'SENT', sentAt: new Date().toISOString() });

    } catch (error) {
        logger.error("Erro fatal ao processar notificação:", error);
        // O erro vai aparecer nos logs do Google Cloud Console
        return snapshot.ref.update({ status: 'ERROR', error: error.toString() });
    }
});