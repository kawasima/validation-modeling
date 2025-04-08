import express from 'express';
import { jobOfferRoutes } from './routes/jobOffer';

const app = express();
const port = process.env.PORT || 3000;

// JSONリクエストをパースするミドルウェア
app.use(express.json());

// ルートの設定
app.use('/api/job-offers', jobOfferRoutes);

// サーバーの起動
app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
}); 