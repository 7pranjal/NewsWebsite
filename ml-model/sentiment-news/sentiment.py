from flask import Flask, request, jsonify
from transformers import pipeline
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # Allow cross-origin if needed

# Load sentiment pipeline (downloads pretrained model)
sentiment_pipeline = pipeline("sentiment-analysis")

@app.route('/analyze', methods=['POST'])
def analyze():
    data = request.get_json()
    text = data.get("text", "")

    if not text.strip():
        return jsonify({"sentiment": "neutral"})

    try:
        result = sentiment_pipeline(text[:512])[0]  
        return jsonify({"sentiment": result['label'].lower()})  
    except Exception as e:
        return jsonify({"sentiment": "neutral", "error": str(e)}), 500

if __name__ == '__main__':
    app.run(host="0.0.0.0", port=5000)
